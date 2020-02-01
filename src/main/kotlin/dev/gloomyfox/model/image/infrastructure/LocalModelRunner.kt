package dev.gloomyfox.model.image.infrastructure

import org.tensorflow.SavedModelBundle
import org.tensorflow.Session
import org.tensorflow.Tensor
import org.tensorflow.framework.ConfigProto
import org.tensorflow.framework.GPUOptions
import java.io.FileNotFoundException
import java.lang.IllegalArgumentException
import java.nio.FloatBuffer

/**
 * Input, Output이 여러 개인 상황이 있을 수 있으나 현재는 한 개만을 가정
 * TODO: 다수의 Input, Output을 받을 수 있게 일반화할 예정
 */
class LocalModelRunner(modelDir: String,
                       private val inputName: String,
                       private val inputShape: LongArray,
                       private val outputName: String,
                       private val outputShape: LongArray,
                       gpuFraction: Double = 1.0) : ModelRunner {

    // ClassPathResource를 사용해도 되지만, Spring 의존을 하지 않고 싶은 부분이어서 아래와 같이 구현
    private val session: Session =
            javaClass.classLoader.getResource(MODEL_DIR_PREFIX + modelDir + MODEL_PB_NAME)?.let {
                javaClass.classLoader.getResource(MODEL_DIR_PREFIX + modelDir + MODEL_VARIABLES_NAME)?.let {
                    SavedModelBundle.loader(it.path.substring(0, it.path.length - MODEL_VARIABLES_NAME.length))
                            .withTags("serve")
                            .withConfigProto(ConfigProto.newBuilder()
                                    .setGpuOptions(GPUOptions.newBuilder()
                                            .setPerProcessGpuMemoryFraction(gpuFraction)).build().toByteArray())
                            .load().session()
                } ?: throw FileNotFoundException("${MODEL_DIR_PREFIX + modelDir + MODEL_VARIABLES_NAME} not found.")
            } ?: throw FileNotFoundException("${MODEL_DIR_PREFIX + modelDir + MODEL_PB_NAME} not found.")

    override fun run(input: FloatArray): FloatArray {
        return session.runner().feed(inputName, input.convert(inputShape)).fetch(outputName).run().convert(outputShape)
    }

    override fun close() {
        this.session.close()
    }

    fun FloatArray.convert(shape: LongArray): Tensor<Float> {
        val shapeSize = if(shape.isEmpty()) 0 else shape.fold(1) { current, next -> current * next.toInt() }

        if(shapeSize != this.size) {
            throw IllegalArgumentException("Size matching fail. " +
                    "Input array size: ${this.size}, shape size: $shapeSize.")
        }

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

    companion object {
        private const val MODEL_DIR_PREFIX = "model/"
        private const val MODEL_PB_NAME = "/saved_model.pb"
        private const val MODEL_VARIABLES_NAME = "/variables"
    }
}