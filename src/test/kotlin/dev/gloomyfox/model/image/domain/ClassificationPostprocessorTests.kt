package dev.gloomyfox.model.image.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

class ClassificationPostprocessorTests {

    private val postprocessor = ClassificationPostprocessor(DUMMY_MODEL_DIR, DUMMY_MODEL_SHAPE)

    @Test
    @DisplayName("생성 시 label 파일이 없으면 FileNotFoundException 발생")
    fun constructLabelFileNotExistTest() {
        Assertions.assertThrows(FileNotFoundException::class.java) {
            ClassificationPostprocessor(DUMMY_WRONG_MODEL_DIR, DUMMY_MODEL_SHAPE)
        }
    }

    @Test
    @DisplayName("생성 시 shape의 차원이 2 미만이면 IllegalArgumentException 발생")
    fun constructShapeDimensionUnderTwoTest() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ClassificationPostprocessor(DUMMY_MODEL_DIR, DUMMY_UNDER_DIMENSION_MODEL_SHAPE)
        }
    }

    @Test
    @DisplayName("생성 시 shape의 차원이 2 초과면 IllegalArgumentException 발생")
    fun constructShapeDimensionOverTwoTest() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ClassificationPostprocessor(DUMMY_MODEL_DIR, DUMMY_OVER_DIMENSION_MODEL_SHAPE)
        }
    }

    @Test
    @DisplayName("생성 시 shape의 배치 크기가 1 미만이면 IllegalArgumentException 발생")
    fun constructShapeBatchUnderOneTest() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ClassificationPostprocessor(DUMMY_MODEL_DIR, DUMMY_UNDER_BATCH_MODEL_SHAPE)
        }
    }

    @Test
    @DisplayName("생성 시 shape의 배치 크기가 1 초과면 IllegalArgumentException 발생")
    fun constructShapeBatchOverOneTest() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ClassificationPostprocessor(DUMMY_MODEL_DIR, DUMMY_OVER_BATCH_MODEL_SHAPE)
        }
    }

    @Test
    @DisplayName("label의 크기와 shape에서 제공된 크기가 다르면 IllegalArgumentException 발생")
    fun constructLabelSizeAndShapeSizeNotEqualTest() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ClassificationPostprocessor(DUMMY_MODEL_DIR, DUMMY_WRONG_SIZE_MODEL_SHAPE)
        }
    }

    @Test
    @DisplayName("process 메소드 테스트")
    fun processTest() {
        val processed = postprocessor.process(DUMMY_PROCESS_FLOAT_ARRAY)
        Assertions.assertEquals(EXPECTED_PROCESS_LABEL, processed.label)
        Assertions.assertEquals(EXPECTED_PROCESS_SCORE, processed.score)
    }

    @Test
    @DisplayName("입력 값이 모두 같을 때 process 메소드 테스트")
    fun processAllEqaulElementTest() {
        val processed = postprocessor.process(DUMMY_PROCESS_ALL_SAME_FLOAT_ARRAY)
        Assertions.assertEquals(EXPECTED_PROCESS_ALL_SAME_LABEL, processed.label)
        Assertions.assertEquals(EXPECTED_PROCESS_SCORE, processed.score)
    }

    @Test
    @DisplayName("모델 출력과 label 크기가 다르면 IllegalArgumentException 발생")
    fun processModelOutputSizeAndLabelSizeNotEqualTest() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            postprocessor.process(DUMMY_WRONG_SIZE_FLOAT_ARRAY)
        }
    }

    companion object {
        private const val DUMMY_MODEL_DIR = "dummy"
        private const val DUMMY_WRONG_MODEL_DIR = ""
        private val DUMMY_MODEL_SHAPE = longArrayOf(1, 10)
        private val DUMMY_UNDER_DIMENSION_MODEL_SHAPE = longArrayOf(10)
        private val DUMMY_OVER_DIMENSION_MODEL_SHAPE = longArrayOf(1, 10, 3)
        private val DUMMY_UNDER_BATCH_MODEL_SHAPE = longArrayOf(0, 10)
        private val DUMMY_OVER_BATCH_MODEL_SHAPE = longArrayOf(2, 10)
        private val DUMMY_WRONG_SIZE_MODEL_SHAPE = longArrayOf(1, 11)
        private val DUMMY_PROCESS_FLOAT_ARRAY = FloatArray(10) { i -> i / 10f }
        private val DUMMY_PROCESS_ALL_SAME_FLOAT_ARRAY = FloatArray(10) { 0.9f }
        private val DUMMY_WRONG_SIZE_FLOAT_ARRAY = FloatArray(9) { i -> i / 9f }

        private const val EXPECTED_PROCESS_LABEL = "9"
        private const val EXPECTED_PROCESS_ALL_SAME_LABEL = "0"
        private const val EXPECTED_PROCESS_SCORE = 0.9f
    }
}