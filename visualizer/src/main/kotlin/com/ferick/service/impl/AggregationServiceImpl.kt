package com.ferick.service.impl

import com.ferick.common.model.AggregatedSiteData
import com.ferick.model.entities.AggregatedSiteDataEntity
import com.ferick.repositories.AggregatedSiteDataRepository
import com.ferick.service.AggregationService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AggregationServiceImpl(
    private val aggregatedSiteDataRepository: AggregatedSiteDataRepository
) : AggregationService {

    override fun saveAggregationSiteData(data: AggregatedSiteData) {
        aggregatedSiteDataRepository.save(
            AggregatedSiteDataEntity(
                startTime = LocalDateTime.parse(data.startTime),
                endTime = LocalDateTime.parse(data.endTime),
                visitorNumber = data.visitorNumber,
                pageStats = data.pageStats
            )
        )
    }
}
