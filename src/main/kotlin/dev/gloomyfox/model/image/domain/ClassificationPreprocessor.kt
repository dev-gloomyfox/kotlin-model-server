package dev.gloomyfox.model.image.domain

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.opencv.global.opencv_imgcodecs.*
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Size

class ClassificationPreprocessor(private val normalizationFunction: (Float) -> (Float),
                                 private val width: Int,
                                 private val height: Int,
                                 private val channel: ColorChannel = ColorChannel.FOUR,
                                 private val order: ColorOrder = ColorOrder.RGB) {

    @ExperimentalUnsignedTypes
    fun process(image: Image): FloatArray {
        // TODO: 기본적 전처리 외의 동작이 필요한 경우(사용자 정의 이미지 전처리)를 위한 작업 예정
        return image.create().convert(channel, order).resize(width, height).normalize(normalizationFunction)
    }

    fun Image.create(): Mat {
        val created = imdecode(Mat(BytePointer(*this.data)), IMREAD_UNCHANGED)
        return if(!created.empty()) created else throw IllegalArgumentException("Cannot convert not image format.")
    }

    fun Mat.convert(channel: ColorChannel, order: ColorOrder): Mat {
        val code = ConvertType.getConvertType(ColorChannel.getColorChannel(this.channels()), channel, order).code
        if(code == COLOR_NONE_CODE) {
            return this
        }

        val converted = Mat()
        cvtColor(this, converted, code)
        this.release()

        return converted
    }

    fun Mat.resize(width: Int, height: Int): Mat {
        val resized = Mat(height, width, this.type())
        resize(this, resized, Size(width, height), 0.toDouble(), 0.toDouble(), INTER_CUBIC)
        this.release()

        return resized
    }

    @ExperimentalUnsignedTypes
    inline fun Mat.normalize(normalizationFunction: (Float) -> Float): FloatArray {
        val bytes = ByteArray(this.cols() * this.rows() * this.channels())
        this.data().get(bytes)
        this.release()

        val floats = FloatArray(bytes.size)
        for(i in floats.indices) {
            floats[i] = normalizationFunction(bytes[i].toUByte().toFloat())
        }

        return floats
    }

    @Suppress("unused")
    private enum class ConvertType(val srcChannel: ColorChannel, val dstChannel: ColorChannel, val order: ColorOrder,
                                   val code: Int) {
        NONE_CASE_ONE(ColorChannel.ONE, ColorChannel.ONE, ColorOrder.BGR, COLOR_NONE_CODE),
        NONE_CASE_TWO(ColorChannel.ONE, ColorChannel.ONE, ColorOrder.RGB, COLOR_NONE_CODE),
        NONE_CASE_THREE(ColorChannel.THREE, ColorChannel.THREE, ColorOrder.BGR, COLOR_NONE_CODE),
        NONE_CASE_FOUR(ColorChannel.FOUR, ColorChannel.FOUR, ColorOrder.BGR, COLOR_NONE_CODE),
        GRAY2BGR(ColorChannel.ONE, ColorChannel.THREE, ColorOrder.BGR, COLOR_GRAY2BGR),
        GRAY2RGB(ColorChannel.ONE, ColorChannel.THREE, ColorOrder.RGB, COLOR_GRAY2RGB),
        GRAY2BGRA(ColorChannel.ONE, ColorChannel.FOUR, ColorOrder.BGR, COLOR_GRAY2BGRA),
        GRAY2RGBA(ColorChannel.ONE, ColorChannel.FOUR, ColorOrder.RGB, COLOR_GRAY2RGBA),
        BGR2GRAY_BGR(ColorChannel.THREE, ColorChannel.ONE, ColorOrder.BGR, COLOR_BGR2GRAY),
        BGR2GRAY_RGB(ColorChannel.THREE, ColorChannel.ONE, ColorOrder.RGB, COLOR_BGR2GRAY),
        BGR2RGB(ColorChannel.THREE, ColorChannel.THREE, ColorOrder.RGB, COLOR_BGR2RGB),
        BGR2BGRA(ColorChannel.THREE, ColorChannel.FOUR, ColorOrder.BGR, COLOR_BGR2BGRA),
        BGR2RGBA(ColorChannel.THREE, ColorChannel.FOUR, ColorOrder.RGB, COLOR_BGR2RGBA),
        BGRA2GRAY_BGR(ColorChannel.FOUR, ColorChannel.ONE, ColorOrder.BGR, COLOR_BGRA2GRAY),
        BGRA2GRAY_RGB(ColorChannel.FOUR, ColorChannel.ONE, ColorOrder.RGB, COLOR_BGRA2GRAY),
        BGRA2BGR(ColorChannel.FOUR, ColorChannel.THREE, ColorOrder.BGR, COLOR_BGRA2BGR),
        BGRA2RGB(ColorChannel.FOUR, ColorChannel.THREE, ColorOrder.RGB, COLOR_BGRA2RGB),
        BGRA2RGBA(ColorChannel.FOUR, ColorChannel.FOUR, ColorOrder.RGB, COLOR_BGRA2RGBA);

        companion object {
            fun getConvertType(srcChannel: ColorChannel, dstChannel: ColorChannel, order: ColorOrder): ConvertType {
                return values().find { it.srcChannel == srcChannel && it.dstChannel == dstChannel &&
                        it.order == order } ?: NONE_CASE_ONE
            }
        }
    }

    companion object {
        private const val COLOR_NONE_CODE = -1
    }
}