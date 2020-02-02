package dev.gloomyfox.model.image.controller

import dev.gloomyfox.model.image.domain.Image
import dev.gloomyfox.model.image.service.ClassificationService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * TODO: 모델 호출을 통신을 이용할 때, WebFlux로 변환 예정
 */
@RestController
class ClassificationController(private val classificationService: ClassificationService) {

    // 별도 Request 객체를 만들면 MultipartFile을 가져오지를 못해서 일단 이렇게 처리, 방법 탐색 중
    @PostMapping("/v1/classify")
    fun classify(file: MultipartFile): ClassificationResponse {
        return ClassificationResponse(classificationService.classify(file.toImage()))
    }

    fun MultipartFile.toImage(): Image {
        return Image(this.bytes)
    }
}