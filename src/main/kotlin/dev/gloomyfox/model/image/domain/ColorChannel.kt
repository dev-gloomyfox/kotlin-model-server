package dev.gloomyfox.model.image.domain

import java.lang.IllegalArgumentException

enum class ColorChannel(val size: Int) {
    ONE(1),
    THREE(3),
    FOUR(4);

    companion object {
        fun getColorChannel(size: Int): ColorChannel {
            return values().find { it.size == size }
                    ?: throw IllegalArgumentException("Channel size $size is incompatible. " +
                    "only 1, 3, 4 channel sizes are compatible.")
        }
    }
}