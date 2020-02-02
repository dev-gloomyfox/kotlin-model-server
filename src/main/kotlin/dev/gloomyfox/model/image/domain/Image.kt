package dev.gloomyfox.model.image.domain

class Image(bytes: ByteArray) {

    val data: ByteArray = bytes

    @ExperimentalUnsignedTypes
    fun classify(model: ClassificationModel): Classification {
        return model.infer(this)
    }
}