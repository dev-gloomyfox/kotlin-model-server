package dev.gloomyfox.model.image.domain.config

import dev.gloomyfox.model.image.domain.ClassificationPostprocessor
import dev.gloomyfox.model.image.domain.ClassificationPreprocessor
import dev.gloomyfox.model.image.domain.SimpleClassificationModel
import dev.gloomyfox.model.image.infrastructure.LocalModelRunner
import dev.gloomyfox.model.image.infrastructure.ModelRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SimpleClassificationModelConfiguration {

    @Bean
    fun simplePreprocessor(): ClassificationPreprocessor {
        return ClassificationPreprocessor(SimpleClassificationModel.PREPROCESS_FUNCTION,
                SimpleClassificationModel.PREPROCESS_WIDTH, SimpleClassificationModel.PREPROCESS_HEIGHT,
                SimpleClassificationModel.PREPROCESS_CHANNEL)
    }

    @Bean
    fun simpleModelRunner(): ModelRunner {
        return LocalModelRunner(SimpleClassificationModel.MODEL_DIR,
                SimpleClassificationModel.MODEL_INPUT_NAME, SimpleClassificationModel.MODEL_INPUT_SHAPE,
                SimpleClassificationModel.MODEL_OUTPUT_NAME, SimpleClassificationModel.MODEL_OUTPUT_SHAPE)
    }

    @Bean
    fun simplePostprocessor(): ClassificationPostprocessor {
        return ClassificationPostprocessor(SimpleClassificationModel.MODEL_DIR,
                SimpleClassificationModel.MODEL_OUTPUT_SHAPE)
    }
}