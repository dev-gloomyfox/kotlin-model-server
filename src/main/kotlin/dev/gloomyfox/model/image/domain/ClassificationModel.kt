package dev.gloomyfox.model.image.domain

abstract class ClassificationModel {

    fun infer(image: Image): Classification {
        return Classification("", 0f)
    }
}