package dev.gloomyfox.model.image.infrastructure

import org.junit.jupiter.api.*
import java.io.FileNotFoundException

/**
 * TODO: 더미 모델을 더 간단한 것으로 변경 예정
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocalModelRunnerTests {

    private lateinit var runner: LocalModelRunner

    @BeforeAll
    fun setUp() {
        this.runner = LocalModelRunner(DUMMY_MODEL_DIR,
        DUMMY_MODEL_INPUT_NAME, DUMMY_INPUT_SHAPE, DUMMY_MODEL_OUTPUT_NAME, DUMMY_OUTPUT_SHAPE)
    }

    @AfterAll
    fun tearDown() {
        this.runner.close()
    }

    @Test
    fun constructPBFileNotExistTest() {
        Assertions.assertThrows(FileNotFoundException::class.java) {
            LocalModelRunner(DUMMY_MODEL_VARIABLES_ONLY_DIR,
                    DUMMY_MODEL_INPUT_NAME, DUMMY_INPUT_SHAPE, DUMMY_MODEL_OUTPUT_NAME, DUMMY_OUTPUT_SHAPE)
        }
    }

    @Test
    fun constructVariableDirectoryNotExistTest() {
        Assertions.assertThrows(FileNotFoundException::class.java) {
            LocalModelRunner(DUMMY_MODEL_PB_ONLY_DIR,
                    DUMMY_MODEL_INPUT_NAME, DUMMY_INPUT_SHAPE, DUMMY_MODEL_OUTPUT_NAME, DUMMY_OUTPUT_SHAPE)
        }
    }

    @Test
    fun runTest() {
        val input = DUMMY_INPUT_ARRAY
        val output = runner.run(input)
        Assertions.assertEquals(EXPECTED_RUN_OUTPUT_SIZE, output.size)
    }

    @Test
    fun extensionFloatArrayConvertTest() {
        with(runner) {
            val tensor = DUMMY_FLOAT_ARRAY.convert(DUMMY_TENSOR_SHAPE)
            Assertions.assertArrayEquals(DUMMY_TENSOR_SHAPE, tensor.shape())

            tensor.copyTo(DUMMY_TENSOR_CONVERTED_ARRAY)

            var count = 0

            for(i in DUMMY_TENSOR_CONVERTED_ARRAY.indices) {
                for(j in DUMMY_TENSOR_CONVERTED_ARRAY[i].indices) {
                    for(k in DUMMY_TENSOR_CONVERTED_ARRAY[i][j].indices) {
                        for(l in DUMMY_TENSOR_CONVERTED_ARRAY[i][j][k].indices) {
                            Assertions.assertEquals(DUMMY_FLOAT_ARRAY[count], DUMMY_TENSOR_CONVERTED_ARRAY[i][j][k][l])
                            count++
                        }
                    }
                }
            }
        }
    }

    @Test
    fun extensionFloatArrayConvertNotEqualShapeSizeTest() {
        with(runner) {
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                DUMMY_FLOAT_ARRAY.convert(DUMMY_WRONG_SIZE_SHAPE)
            }
        }
    }

    @Test
    fun extensionTensorListConvertTest() {
        with(runner) {
            val tensor = DUMMY_FLOAT_ARRAY.convert(DUMMY_TENSOR_SHAPE)

            val tensorList = List(1) {
                tensor
            }

            val convertedArray = tensorList.convert(DUMMY_TENSOR_SHAPE)
            Assertions.assertArrayEquals(DUMMY_FLOAT_ARRAY, convertedArray)
        }
    }

    @Test
    fun extensionTensorListConvertNotEqualShapeTest() {
        with(runner) {
            val tensor = DUMMY_FLOAT_ARRAY.convert(DUMMY_TENSOR_SHAPE)

            val tensorList = List(1) {
                tensor
            }

            Assertions.assertThrows(IllegalArgumentException::class.java) {
                tensorList.convert(DUMMY_WRONG_SHAPE)
            }
        }
    }

    companion object {
        private const val DUMMY_MODEL_DIR = "dummy"
        private const val DUMMY_MODEL_PB_ONLY_DIR = "only_pb"
        private const val DUMMY_MODEL_VARIABLES_ONLY_DIR = "only_variables"
        private const val DUMMY_MODEL_INPUT_NAME = "serving_default_dense_12_input:0"
        private const val DUMMY_MODEL_OUTPUT_NAME = "StatefulPartitionedCall:0"
        private val DUMMY_INPUT_SHAPE = longArrayOf(1, 784)
        private val DUMMY_OUTPUT_SHAPE = longArrayOf(1, 10)
        private val DUMMY_INPUT_ARRAY = FloatArray(DUMMY_INPUT_SHAPE[1].toInt()) { i -> i / (28 * 28).toFloat() }
        private val DUMMY_FLOAT_ARRAY = floatArrayOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f)
        private val DUMMY_TENSOR_SHAPE = longArrayOf(1, 3, 3, 1)
        private val DUMMY_TENSOR_CONVERTED_ARRAY =
                Array(1) {
                    Array(3) {
                        Array(3) {
                            FloatArray(1)
                        }
                    }
                }
        private val DUMMY_WRONG_SIZE_SHAPE = longArrayOf(1, 3, 2, 1)
        private val DUMMY_WRONG_SHAPE = longArrayOf(1, 1, 3, 3)

        private const val EXPECTED_RUN_OUTPUT_SIZE = 10
    }
}