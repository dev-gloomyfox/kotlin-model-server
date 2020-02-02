package dev.gloomyfox.model.image.domain

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import dev.gloomyfox.model.image.infrastructure.ModelRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ClassificationModelTests {

    class DummyClassificationModel(preprocessor: ClassificationPreprocessor,
                                   modelRunner: ModelRunner,
                                   postprocessor: ClassificationPostprocessor)
        : ClassificationModel(preprocessor, modelRunner, postprocessor)



    @ExperimentalUnsignedTypes
    private val preprocessor = mock<ClassificationPreprocessor> {
        on {
            process(MOCK_IMAGE)
        } doReturn MOCK_PREPROCESSED
    }

    private val modelRunner = mock<ModelRunner> {
        on {
            run(MOCK_PREPROCESSED)
        } doReturn MOCK_RAN
    }

    private val postprocessor = mock<ClassificationPostprocessor> {
        on {
            process(MOCK_RAN)
        } doReturn MOCK_POSTPROCESSED
    }

    @ExperimentalUnsignedTypes
    private val model = DummyClassificationModel(preprocessor, modelRunner, postprocessor)

    @ExperimentalUnsignedTypes
    @Test
    @DisplayName("infer 메소드 테스트")
    fun inferTest() {
        val inferred = model.infer(MOCK_IMAGE)
        Assertions.assertEquals(MOCK_POSTPROCESSED.label, inferred.label)
        Assertions.assertEquals(MOCK_POSTPROCESSED.score, inferred.score)
    }

    companion object {
        private val MOCK_IMAGE = mock<Image>()
        private val MOCK_PREPROCESSED = FloatArray(3 * 3)
        private val MOCK_RAN = FloatArray(3)
        private val MOCK_POSTPROCESSED = Classification("dummy", 1.0f)
    }
}