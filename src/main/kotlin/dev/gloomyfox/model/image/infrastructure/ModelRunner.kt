package dev.gloomyfox.model.image.infrastructure

interface ModelRunner {
    // 더 일반화할 수 있으나, 현 시점에서는 이렇게 진행
    fun run(input: FloatArray): FloatArray
}