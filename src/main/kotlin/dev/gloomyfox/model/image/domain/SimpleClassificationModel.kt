package dev.gloomyfox.model.image.domain

import dev.gloomyfox.model.image.infrastructure.ModelRunner
import org.springframework.stereotype.Component

@Component
class SimpleClassificationModel(preprocessor: ClassificationPreprocessor,
                                modelRunner: ModelRunner,
                                postprocessor: ClassificationPostprocessor)
    : ClassificationModel(preprocessor, modelRunner, postprocessor) {

    /**
     * saved_model_cli show --dir ${dir_path} --tag_set serve --signature_def serving_default
     *
     * The given SavedModel SignatureDef contains the following input(s):
     * inputs['dense_12_input'] tensor_info:
     * dtype: DT_FLOAT
     * shape: (-1, 784)
     * name: serving_default_dense_12_input:0
     * The given SavedModel SignatureDef contains the following output(s):
     * outputs['dense_13'] tensor_info:
     * dtype: DT_FLOAT
     * shape: (-1, 10)
     * name: StatefulPartitionedCall:0
     * Method name is: tensorflow/serving/predict
     */

    companion object {
        val PREPROCESS_FUNCTION = { i: Float -> i / 255.0f }
        val PREPROCESS_WIDTH = 28
        val PREPROCESS_HEIGHT = 28
        val PREPROCESS_CHANNEL = ColorChannel.ONE
        val MODEL_DIR = "classification/simple"
        val MODEL_INPUT_NAME = "serving_default_dense_12_input:0"
        val MODEL_INPUT_SHAPE = longArrayOf(1, 784)
        val MODEL_OUTPUT_NAME = "StatefulPartitionedCall:0"
        val MODEL_OUTPUT_SHAPE = longArrayOf(1, 10)
    }
}