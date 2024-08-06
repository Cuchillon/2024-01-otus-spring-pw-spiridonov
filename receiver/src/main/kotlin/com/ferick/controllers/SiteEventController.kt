package com.ferick.controllers

import com.ferick.common.model.SiteEventDto
import com.ferick.service.SiteEventService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SiteEventController(
    private val siteEventService: SiteEventService
) {

    @PostMapping("/site-event")
    fun createSiteEvent(
        @RequestBody event: SiteEventDto
    ) = siteEventService.sendEvent(event)
}
