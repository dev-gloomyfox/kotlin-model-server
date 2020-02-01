package dev.gloomyfox.model.image.infrastructure

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LocalModelRunnerTests {

    private val runner = LocalModelRunner("dummy", "", longArrayOf(1, 3, 3, 1),
            "", longArrayOf(1, 10))

    @Test
    fun extensionConvertTest() {
        with(runner) {
            val floats = floatArrayOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f)
            val tensor = floats.convert(longArrayOf(1, 3, 3, 1))
            Assertions.assertArrayEquals(longArrayOf(1, 3, 3, 1), tensor.shape())

            val converted =
                    Array(1) {
                        Array(3) {
                            Array(3) {
                                FloatArray(1)
                            }
                        }
                    }

            tensor.copyTo(converted)

            var count = 0

            for(i in converted.indices) {
                for(j in converted[i].indices) {
                    for(k in converted[i][j].indices) {
                        for(l in converted[i][j][k].indices) {
                            Assertions.assertEquals(floats[count], converted[i][j][k][l])
                            count++
                        }
                    }
                }
            }
        }
    }
}