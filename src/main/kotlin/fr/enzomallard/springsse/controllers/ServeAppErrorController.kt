package fr.enzomallard.springsse.controllers

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import java.io.IOException

@Controller
class ServeAppErrorController : ErrorController {

    @RequestMapping("/error")
    fun handleError(): ResponseEntity<Resource> {
        val resource: Resource = ClassPathResource("/static/index.html")
        return try {
            ResponseEntity.ok().body(resource)
        } catch (e: IOException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}
