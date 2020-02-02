package dev.gloomyfox.model.image.domain

import dev.gloomyfox.model.image.infrastructure.ModelRunner

abstract class ClassificationModel(private val preprocessor: ClassificationPreprocessor,
                                   private val modelRunner: ModelRunner,
                                   private val postprocessor: ClassificationPostprocessor) {

    @ExperimentalUnsignedTypes
    fun infer(image: Image): Classification {
        return postprocessor.process(modelRunner.run(preprocessor.process(image)))
    }
}