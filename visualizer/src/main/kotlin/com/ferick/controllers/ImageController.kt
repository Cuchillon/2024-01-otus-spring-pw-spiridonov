package com.ferick.controllers

import com.ferick.exceptions.ImageLoadingException
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Paths

@RestController
class ImageController {

    @GetMapping("/images/{name}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun downloadImage(@PathVariable name: String): Resource {
        val image = try {
            Files.readAllBytes(imageDir.resolve(name))
        } catch (e: Exception) {
            throw ImageLoadingException("Failed to load image $name")
        }
        return ByteArrayResource(image)
    }

    companion object {
        private val imageDir = Paths.get("lets-plot-images")
    }
}
