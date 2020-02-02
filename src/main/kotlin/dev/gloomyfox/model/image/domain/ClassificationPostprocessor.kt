package dev.gloomyfox.model.image.domain

import java.io.File
import java.io.FileNotFoundException
import java.lang.IllegalArgumentException

/**
 * 단순한 수준으로 구성, Postprocess는 전처리처럼 일반화 할 수 있는 부분이 많지는 않은 듯
 */
class ClassificationPostprocessor(modelDir: String, shape: LongArray) {

    private val labels: List<String>

    init {
        if(shape.size != 2) {
            throw IllegalArgumentException("Output shape dimension ${shape.size} incompatible. " +
                    "Only 2 dimension compatible.")
        } else if(shape[0].toInt() != 1) {
            throw IllegalArgumentException("Output batch size ${shape[0].toInt()} incompatible. " +
                    "Only 1 batch size compatible")
        }

        this.labels = javaClass.classLoader.getResource(MODEL_DIR_PREFIX + modelDir + MODEL_LABEL_NAME)?.let {
            url -> File(url.file).useLines { it.toList() }
        } ?: throw FileNotFoundException("${MODEL_DIR_PREFIX + modelDir + MODEL_LABEL_NAME} not found.")

        if(this.labels.size != shape[1].toInt()) {
            throw IllegalArgumentException("Label size and shape size not equal. " +
                    "Label size: ${this.labels.size}, shape size: ${shape[1].toInt()}.")
        }
    }

    fun process(inferred: FloatArray): Classification {
        if(inferred.size != labels.size) {
            throw IllegalArgumentException("Model output size and label size not equal. " +
                    "Model size: ${inferred.size}, label size: ${labels.size}.")
        }
        val index = inferred.max()?.let { inferred.indexOf(it) }
                ?: throw IllegalArgumentException("Do not find model output's max value.")

        return Classification(labels[index], inferred[index])
    }

    companion object {
        private const val MODEL_DIR_PREFIX = "model/"
        private const val MODEL_LABEL_NAME = "/label_map.txt"
    }
}