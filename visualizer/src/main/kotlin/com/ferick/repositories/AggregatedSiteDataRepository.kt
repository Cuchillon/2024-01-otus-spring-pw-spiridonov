package com.ferick.repositories

import com.ferick.model.entities.AggregatedSiteDataEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime

interface AggregatedSiteDataRepository : MongoRepository<AggregatedSiteDataEntity, String> {

    fun findByStartTimeBetween(start: LocalDateTime, end: LocalDateTime): List<AggregatedSiteDataEntity>

    fun findFirstByOrderByStartTimeAsc(): AggregatedSiteDataEntity?

    fun findFirstByOrderByStartTimeDesc(): AggregatedSiteDataEntity?
}
