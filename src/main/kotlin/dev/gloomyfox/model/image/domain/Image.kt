package dev.gloomyfox.model.image.domain

class Image(bytes: ByteArray) {

    val data: ByteArray = bytes

    fun classify(model: ClassificationModel): Classification {
        return model.infer(this)
    }
}