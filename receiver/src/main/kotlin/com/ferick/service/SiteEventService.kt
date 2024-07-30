package com.ferick.service

import com.ferick.common.model.SiteEvent
import com.ferick.model.SiteEventDto
import reactor.core.publisher.Mono

interface SiteEventService {
    fun sendEvent(event: SiteEventDto): Mono<SiteEvent>
}
