package com.ferick.controllers

import com.ferick.model.dto.PeriodRequest
import com.ferick.service.VisualizingService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class MainPageController(
    private val visualizingService: VisualizingService
) {

    @GetMapping("/")
    fun getPeriod(model: Model): String {
        val (startTime, endTime) = visualizingService.getAvailablePeriod()
        model.addAttribute("startTime", startTime)
        model.addAttribute("endTime", endTime)
        model.addAttribute("request", PeriodRequest())
        return "index"
    }

    @PostMapping("/period")
    fun sendPeriod(
        @Valid @ModelAttribute("request") request: PeriodRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            return "index"
        }
        val imagePaths = visualizingService.visualizeDataBetween(request)
        return "data"
    }
}
