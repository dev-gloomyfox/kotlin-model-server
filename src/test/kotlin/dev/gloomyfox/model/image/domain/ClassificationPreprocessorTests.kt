package dev.gloomyfox.model.image.domain

import org.bytedeco.javacpp.indexer.UByteRawIndexer
import org.bytedeco.opencv.global.opencv_core.*
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Scalar
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URL

/**
 * TODO: 현재 내부 값의 변화는 OpenCV가 올바를 것이라고 가정을 한 것이어서,
 *  차후 값이 잘 맞는지를 확인하는 테스트 추가 예정(process, Mat.convert, Mat.resize)
 *
 * TODO: indexer에서 값을 가져오고 비교할 때, 한 번에 가져와서 비교하는 것이 성능적으로 더 유리할 것으로 생각되어 차후 실험 후 변경 예정
 */
class ClassificationPreprocessorTests {

    private val preprocessor = ClassificationPreprocessor(DUMMY_NORMALIZE_FUNCTION,
            DUMMY_PREPROCESS_WIDTH, DUMMY_PREPROCESS_HEIGHT, DUMMY_PREPROCESS_CHANNEL)

    private val assertSame: (Mat, Mat) -> Unit = { src: Mat, converted: Mat ->
        Assertions.assertSame(src, converted)
    }

    private val assertOneChannelDestination: (Mat, Mat) -> Unit = { src: Mat, converted: Mat ->
        assertConvertChannel(src, converted, ColorChannel.ONE)
    }

    private val assertThreeChannelDestination: (Mat, Mat) -> Unit = { src: Mat, converted: Mat ->
        assertConvertChannel(src, converted, ColorChannel.THREE)
    }

    private val assertFourChannelDestination: (Mat, Mat) -> Unit = { src: Mat, converted: Mat ->
        assertConvertChannel(src, converted, ColorChannel.FOUR)
    }

    @ExperimentalUnsignedTypes
    @Test
    @DisplayName("process 메소드 테스트")
    fun processTest() {
        val processed = preprocessor.process(Image(File(getURL(DUMMY_PNG_NAME).file).readBytes()))

        Assertions.assertEquals(DUMMY_PREPROCESS_WIDTH * DUMMY_PREPROCESS_HEIGHT *
                DUMMY_PREPROCESS_CHANNEL.size, processed.size)
    }

    @Test
    @DisplayName("Image.create extension 메소드 PNG 포맷 테스트")
    fun extensionCreatePNGFormatTest() {
        extensionCreateImageFormatTest(ImageFormat.PNG)
    }

    @Test
    @DisplayName("Image.create extension 메소드 JPG 포맷 테스트")
    fun extensionCreateJPGFormatTest() {
        extensionCreateImageFormatTest(ImageFormat.JPG)
    }

    @Test
    @DisplayName("Image.create extension 메소드 이미지 포맷이 아닐 때, IllegalArgumentException 발생")
    fun extensionCreateNotImageFormatTest() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            with(preprocessor) {
                Image(File(getURL(DUMMY_NOT_IMAGE_NAME).file).readBytes()).create()
            }
        }
    }

    @Test
    @DisplayName("Mat.convert extension 메소드 1 to 1 채널 테스트")
    fun extensionConvertOneToOneChannelTest() {
        extensionConvertTest(ColorChannel.ONE, ColorChannel.ONE, ColorOrder.RGB, assertSame)
        extensionConvertTest(ColorChannel.ONE, ColorChannel.ONE, ColorOrder.BGR, assertSame)
    }

    @Test
    @DisplayName("Mat.convert extension 메소드 1 to 3 채널 테스트")
    fun extensionConvertOneToThreeChannelTest() {
        extensionConvertTest(ColorChannel.ONE, ColorChannel.THREE, ColorOrder.BGR, assertThreeChannelDestination)
        extensionConvertTest(ColorChannel.ONE, ColorChannel.THREE, ColorOrder.RGB, assertThreeChannelDestination)
    }

    @Test
    @DisplayName("Mat.convert extension 메소드 1 to 4 채널 테스트")
    fun extensionConvertOneToFourChannelTest() {
        extensionConvertTest(ColorChannel.ONE, ColorChannel.FOUR, ColorOrder.RGB, assertFourChannelDestination)
        extensionConvertTest(ColorChannel.ONE, ColorChannel.FOUR, ColorOrder.BGR, assertFourChannelDestination)
    }

    @Test
    @DisplayName("Mat.convert extension 메소드 3 to 1 채널 테스트")
    fun extensionConvertThreeToOneChannelTest() {
        extensionConvertTest(ColorChannel.THREE, ColorChannel.ONE, ColorOrder.BGR, assertOneChannelDestination)
        extensionConvertTest(ColorChannel.THREE, ColorChannel.ONE, ColorOrder.RGB, assertOneChannelDestination)
    }

    @Test
    @DisplayName("Mat.convert extension 메소드 3 to 3 채널 테스트")
    fun extensionConvertThreeToThreeChannelTest() {
        extensionConvertTest(ColorChannel.THREE, ColorChannel.THREE, ColorOrder.BGR, assertSame)
        extensionConvertTest(ColorChannel.THREE, ColorChannel.THREE, ColorOrder.RGB, assertThreeChannelDestination)
    }

    @Test
    @DisplayName("Mat.convert extension 메소드 3 to 4 채널 테스트")
    fun extensionConvertThreeToFourChannelTest() {
        extensionConvertTest(ColorChannel.THREE, ColorChannel.FOUR, ColorOrder.BGR, assertFourChannelDestination)
        extensionConvertTest(ColorChannel.THREE, ColorChannel.FOUR, ColorOrder.RGB, assertFourChannelDestination)
    }

    @Test
    @DisplayName("Mat.convert extension 메소드 4 to 1 채널 테스트")
    fun extensionConvertFourToOneChannelTest() {
        extensionConvertTest(ColorChannel.FOUR, ColorChannel.ONE, ColorOrder.BGR, assertOneChannelDestination)
        extensionConvertTest(ColorChannel.FOUR, ColorChannel.ONE, ColorOrder.RGB, assertOneChannelDestination)
    }

    @Test
    @DisplayName("Mat.convert extension 메소드 4 to 3 채널 테스트")
    fun extensionConvertFourToThreeChannelTest() {
        extensionConvertTest(ColorChannel.FOUR, ColorChannel.THREE, ColorOrder.BGR, assertThreeChannelDestination)
        extensionConvertTest(ColorChannel.FOUR, ColorChannel.THREE, ColorOrder.RGB, assertThreeChannelDestination)
    }

    @Test
    @DisplayName("Mat.convert extension 메소드 4 to 4 채널 테스트")
    fun extensionConvertFourToFourChannelTest() {
        extensionConvertTest(ColorChannel.FOUR, ColorChannel.FOUR, ColorOrder.BGR, assertSame)
        extensionConvertTest(ColorChannel.FOUR, ColorChannel.FOUR, ColorOrder.RGB, assertFourChannelDestination)
    }

    @Test
    @DisplayName("Mat.convert extension 메소드 호환되지 않는 채널 변환일 때, IllegalArgumentException 발생")
    fun extensionConvertIncompatibleSourceChannelTest() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            with(preprocessor) {
                val mat = Mat(DUMMY_CONVERT_MAT_HEIGHT, DUMMY_CONVERT_MAT_WIDTH, CV_8UC2)
                mat.convert(ColorChannel.ONE, ColorOrder.BGR)
            }
        }
    }

    @Test
    @DisplayName("Mat.resize extension 메소드 테스트")
    fun extensionResizeTest() {
        with(preprocessor) {
            val mat = Mat(DUMMY_RESIZE_MAT_HEIGHT, DUMMY_RESIZE_MAT_WIDTH, CV_8UC4,
                    Scalar(EXPECTED_RESIZE_PIXEL_VALUE.toDouble(), EXPECTED_RESIZE_PIXEL_VALUE.toDouble(),
                            EXPECTED_RESIZE_PIXEL_VALUE.toDouble(), EXPECTED_RESIZE_PIXEL_VALUE.toDouble()))
            val resized = mat.resize(EXPECTED_RESIZE_WIDTH, EXPECTED_RESIZE_HEIGHT)

            Assertions.assertNull(mat.data())

            Assertions.assertEquals(EXPECTED_RESIZE_WIDTH, resized.cols())
            Assertions.assertEquals(EXPECTED_RESIZE_HEIGHT, resized.rows())

            val indexer = resized.createIndexer<UByteRawIndexer>()

            for(i in 0 until resized.rows()) {
                for(j in 0 until resized.cols()) {
                    Assertions.assertEquals(EXPECTED_RESIZE_PIXEL_VALUE, indexer.get(i.toLong(), j.toLong()))
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    @Test
    @DisplayName("Mat.normalize 메소드 테스트")
    fun extensionNormalizeTest() {
        with(preprocessor) {
            val mat = Mat(DUMMY_NORMALIZE_MAT_HEIGHT, DUMMY_NORMALIZE_MAT_WIDTH, CV_8UC4,
                    Scalar(DUMMY_NORMALIZE_MAT_VALUE.toDouble(), DUMMY_NORMALIZE_MAT_VALUE.toDouble(),
                            DUMMY_NORMALIZE_MAT_VALUE.toDouble(), DUMMY_NORMALIZE_MAT_VALUE.toDouble()))
            val normalized = mat.normalize(DUMMY_NORMALIZE_FUNCTION)

            Assertions.assertNull(mat.data())

            Assertions.assertEquals(DUMMY_NORMALIZE_MAT_WIDTH * DUMMY_NORMALIZE_MAT_HEIGHT * 4,
                    normalized.size)
            for(i in normalized.indices) {
                Assertions.assertEquals(EXPECTED_NORMALIZE_VALUE, normalized[i])
            }
        }
    }

    private enum class ImageFormat {
        PNG,
        JPG
    }

    private fun getURL(resourceName: String): URL {
        return javaClass.classLoader.getResource(resourceName)
                ?: throw IllegalArgumentException("Resource is null.")
    }

    private fun extensionCreateImageFormatTest(format: ImageFormat) {
        val resourceName = if(format == ImageFormat.PNG) DUMMY_PNG_NAME else DUMMY_JPG_NAME

        val expectedBluePixelValue = if(format == ImageFormat.PNG)
            EXPECTED_PNG_BLUE_PIXEL_VALUE else EXPECTED_JPG_BLUE_PIXEL_VALUE
        val expectedGreenPixelValue = if(format == ImageFormat.PNG)
            EXPECTED_PNG_GREEN_PIXEL_VALUE else EXPECTED_JPG_GREEN_PIXEL_VALUE
        val expectedRedPixelValue = if(format == ImageFormat.PNG)
            EXPECTED_PNG_RED_PIXEL_VALUE else EXPECTED_JPG_RED_PIXEL_VALUE
        val expectedChannelSize = if(format == ImageFormat.PNG)
            EXPECTED_PNG_CHANNEL_SIZE else EXPECTED_JPG_CHANNEL_SIZE

        with(preprocessor) {
            val mat = Image(File(getURL(resourceName).file).readBytes()).create()

            Assertions.assertEquals(EXPECTED_WIDTH, mat.cols())
            Assertions.assertEquals(EXPECTED_HEIGHT, mat.rows())
            Assertions.assertEquals(expectedChannelSize, mat.channels())

            val indexer = mat.createIndexer<UByteRawIndexer>()

            for (i in 0 until mat.rows()) {
                for(j in 0 until mat.cols()) {
                    when(j % mat.channels()) {
                        0 -> {
                            Assertions.assertEquals(expectedBluePixelValue, indexer.get(i.toLong(), j.toLong()))
                        }

                        1 -> {
                            Assertions.assertEquals(expectedGreenPixelValue, indexer.get(i.toLong(), j.toLong()))
                        }

                        2 -> {
                            Assertions.assertEquals(expectedRedPixelValue, indexer.get(i.toLong(), j.toLong()))
                        }

                        3 -> {
                            Assertions.assertEquals(EXPECTED_PNG_ALPHA_PIXEL_VALUE, indexer.get(i.toLong(), j.toLong()))
                        }
                    }

                }
            }
        }
    }

    private inline fun extensionConvertTest(srcChannel: ColorChannel, dstChannel: ColorChannel, order: ColorOrder,
                                            assertFunction: (Mat, Mat) -> Unit) {
        val type = when(srcChannel) {
            ColorChannel.ONE -> CV_8UC1
            ColorChannel.THREE -> CV_8UC3
            ColorChannel.FOUR -> CV_8UC4
        }

        with(preprocessor) {
            val mat = Mat(DUMMY_CONVERT_MAT_HEIGHT, DUMMY_CONVERT_MAT_WIDTH, type)
            val converted = mat.convert(dstChannel, order)
            assertFunction(mat, converted)
        }
    }

    private fun assertConvertChannel(src: Mat, converted: Mat, dstChannel: ColorChannel) {
        Assertions.assertEquals(dstChannel.size, converted.channels())
        Assertions.assertNull(src.data())
    }

    companion object {
        private const val DUMMY_PREPROCESS_WIDTH = 28
        private const val DUMMY_PREPROCESS_HEIGHT = 28
        private const val DUMMY_JPG_NAME = "dummy.jpg"
        private const val DUMMY_PNG_NAME = "dummy.png"
        private const val DUMMY_NOT_IMAGE_NAME = "dummy.txt"
        private const val DUMMY_CONVERT_MAT_WIDTH = 3
        private const val DUMMY_CONVERT_MAT_HEIGHT = 3
        private const val DUMMY_RESIZE_MAT_WIDTH = 300
        private const val DUMMY_RESIZE_MAT_HEIGHT = 300
        private const val DUMMY_NORMALIZE_MAT_WIDTH = 300
        private const val DUMMY_NORMALIZE_MAT_HEIGHT = 300
        private const val DUMMY_NORMALIZE_MAT_VALUE = 255

        private const val EXPECTED_WIDTH = 9
        private const val EXPECTED_HEIGHT = 16
        private const val EXPECTED_PNG_CHANNEL_SIZE = 4
        private const val EXPECTED_JPG_CHANNEL_SIZE = 3
        private const val EXPECTED_PNG_BLUE_PIXEL_VALUE = 150
        private const val EXPECTED_PNG_GREEN_PIXEL_VALUE = 100
        private const val EXPECTED_PNG_RED_PIXEL_VALUE = 50
        private const val EXPECTED_PNG_ALPHA_PIXEL_VALUE = 255
        private const val EXPECTED_JPG_BLUE_PIXEL_VALUE = 149
        private const val EXPECTED_JPG_GREEN_PIXEL_VALUE = 100
        private const val EXPECTED_JPG_RED_PIXEL_VALUE = 50
        private const val EXPECTED_RESIZE_WIDTH = 10
        private const val EXPECTED_RESIZE_HEIGHT = 20
        private const val EXPECTED_RESIZE_PIXEL_VALUE = 255
        private const val EXPECTED_NORMALIZE_VALUE = 51.0f

        private val DUMMY_PREPROCESS_CHANNEL = ColorChannel.FOUR
        private val DUMMY_NORMALIZE_FUNCTION = { x: Float -> x / 5.0f }
    }
}