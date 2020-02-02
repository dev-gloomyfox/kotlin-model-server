package dev.gloomyfox.model.image.service

import dev.gloomyfox.model.image.domain.Classification
import dev.gloomyfox.model.image.domain.Image

interface ClassificationService {

    fun classify(image: Image): Classification
}