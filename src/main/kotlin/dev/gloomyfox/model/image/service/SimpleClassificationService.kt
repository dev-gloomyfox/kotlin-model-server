package dev.gloomyfox.model.image.service

import dev.gloomyfox.model.image.domain.Classification
import dev.gloomyfox.model.image.domain.ClassificationModel
import dev.gloomyfox.model.image.domain.Image
import org.springframework.stereotype.Service

@Service
class SimpleClassificationService(private val classificationModel: ClassificationModel) : ClassificationService {

    @ExperimentalUnsignedTypes
    override fun classify(image: Image): Classification {
        return image.classify(classificationModel)
    }
}