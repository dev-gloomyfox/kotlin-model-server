package dev.gloomyfox.model.image.domain

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class ImageTests {

    @Test
    fun classifyTest() {
        val url = javaClass.classLoader.getResource(DUMMY_FILE_NAME)
                ?: throw IllegalArgumentException("Resource is null.")
        val image = Image(File(url.file).readBytes())
        val mockedClassificationModel = mock<ClassificationModel> {
            on {
                infer(image)
            } doReturn Classification("mock", 1.0f)
        }

        val classification = image.classify(mockedClassificationModel)

        Assertions.assertEquals(EXPECTED_CLASSIFICATION_LABEL, classification.label)
        Assertions.assertEquals(EXPECTED_CLASSIFICATION_SCORE, classification.score)
    }

    companion object {
        private const val DUMMY_FILE_NAME = "dummy.jpg"
        private const val EXPECTED_CLASSIFICATION_LABEL = "mock"
        private const val EXPECTED_CLASSIFICATION_SCORE = 1.0f
    }
}
