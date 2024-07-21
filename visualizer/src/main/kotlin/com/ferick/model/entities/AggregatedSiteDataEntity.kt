package com.ferick.model.entities

import com.ferick.common.model.PageStat
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("aggregatedSiteData")
class AggregatedSiteDataEntity(
    @Id
    var id: String? = null,
    @Indexed
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val visitorNumber: Long,
    val pageStats: Map<String, PageStat>
)
