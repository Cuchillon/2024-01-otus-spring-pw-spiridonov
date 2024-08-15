package com.ferick.converters

import com.ferick.common.model.AggregatedSiteData
import com.ferick.model.entities.AggregatedSiteDataEntity
import org.springframework.stereotype.Component

@Component
class AggregatedSiteDataConverter {

    fun entityToDto(entity: AggregatedSiteDataEntity): AggregatedSiteData =
        AggregatedSiteData(
            startTime = entity.startTime.toString(),
            endTime = entity.endTime.toString(),
            visitorNumber = entity.visitorNumber,
            pageStats = entity.pageStats
        )
}
