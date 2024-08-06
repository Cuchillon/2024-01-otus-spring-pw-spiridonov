package com.ferick.controllers

import com.ferick.exceptions.NoSiteDataException
import com.ferick.model.dto.PeriodRequest
import com.ferick.model.dto.PlotType
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
    fun getPeriod(model: Model): String =
        try {
            val (startTime, endTime) = visualizingService.getAvailablePeriod()
            model.addAttribute("startTime", startTime)
            model.addAttribute("endTime", endTime)
            model.addAttribute("request", PeriodRequest())
            "index"
        } catch (e: NoSiteDataException) {
            "empty"
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
        val periodData = visualizingService.visualizeDataBetween(request)
        model.addAttribute("startTime", periodData.startTime)
        model.addAttribute("endTime", periodData.endTime)
        model.addAttribute(
            "visitors", periodData.imageNames[PlotType.PAGE_VISITORS_COUNT]!![0]
        )
        model.addAttribute("viewCounts", periodData.imageNames[PlotType.PAGE_VIEW_COUNT]!!)
        model.addAttribute("viewPeriods", periodData.imageNames[PlotType.PAGE_VIEW_PERIOD]!!)
        return "data"
    }
}
