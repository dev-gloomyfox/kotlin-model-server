package dev.gloomyfox.model

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ModelServerApplication

fun main(args: Array<String>) {
    runApplication<ModelServerApplication>(*args)
}
