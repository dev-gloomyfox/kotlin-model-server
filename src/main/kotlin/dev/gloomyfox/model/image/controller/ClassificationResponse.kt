package dev.gloomyfox.model.image.controller

import dev.gloomyfox.model.image.domain.Classification

@Suppress("unused")
class ClassificationResponse(classification: Classification) {

    val label: String = classification.label
    val percentage: String = (classification.score * 100).toString() + "%"
}