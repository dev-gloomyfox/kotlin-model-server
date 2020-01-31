package dev.gloomyfox.model.image.infrastructure

import org.tensorflow.SavedModelBundle
import org.tensorflow.Session
import org.tensorflow.Tensor
import org.tensorflow.framework.ConfigProto
import org.tensorflow.framework.GPUOptions
import java.lang.IllegalArgumentException
import java.nio.FloatBuffer

// Input, Output이 여러 개인 상황이 있을 수 있으나 현재는 한 개만을 가정
class LocalModelRunner(modelDir: String,
                       private val inputName: String,
                       private val inputShape: LongArray,
                       private val outputName: String,
                       private val outputShape: LongArray,
                       gpuFraction: Double = 1.0) : ModelRunner {

    // 모델을 찾지 못하면 native에서 크래시가 발생하나 끌어올리는 코드가 없어 원인을 알기 어려운 문제 존재
    private val session: Session = SavedModelBundle.loader(modelDir)
            .withConfigProto(ConfigProto.newBuilder()
                    .setGpuOptions(GPUOptions.newBuilder()
                            .setPerProcessGpuMemoryFraction(gpuFraction)).build().toByteArray())
            .load().session()

    override fun run(input: FloatArray): FloatArray {
        return session.runner().feed(inputName, input.convert(inputShape)).fetch(outputName).run().convert(outputShape)
    }

    fun FloatArray.convert(shape: LongArray): Tensor<Float> {
        return Tensor.create(shape, FloatBuffer.wrap(this))
    }

    fun List<Tensor<*>>.convert(shape: LongArray): FloatArray {
        if(!(this[0].shape() ?: throw IllegalArgumentException("Source shape is null.")).contentEquals(shape)) {
            throw IllegalArgumentException("Shape matching fail. Expect $shape but actual ${this[0].shape()}")
        }

        val buffer = FloatBuffer.allocate(this[0].numElements())
        this[0].writeTo(buffer)

        return buffer.array()
    }
}