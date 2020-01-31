package dev.gloomyfox.model.image.domain

import org.springframework.stereotype.Component

@Component
class SimpleClassificationModel: ClassificationModel() {

    /**
     * saved_model_cli show --dir ${dir_path} --tag_set serve --signature_def serving_default
     *
     * The given SavedModel SignatureDef contains the following input(s):
     * inputs['digits'] tensor_info:
     * dtype: DT_FLOAT
     * shape: (-1, 784)
     * name: serving_default_digits:0
     * The given SavedModel SignatureDef contains the following output(s):
     * outputs['predictions'] tensor_info:
     * dtype: DT_FLOAT
     * shape: (-1, 10)
     * name: StatefulPartitionedCall:0
     * Method name is: tensorflow/serving/predict
     */
}