package com.ferick

import com.ferick.generator.AggregationGenerator
import com.ferick.model.entities.AggregatedSiteDataEntity
import com.ferick.repositories.AggregatedSiteDataRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class VisualizerApplicationTests {

	@Autowired
	private lateinit var aggregatedSiteDataRepository: AggregatedSiteDataRepository

	@Test
	fun contextLoads() {
		val aggregatedSiteData = AggregationGenerator.generate()
		aggregatedSiteDataRepository.saveAll(
			aggregatedSiteData.map {
				AggregatedSiteDataEntity(
					startTime = LocalDateTime.parse(it.startTime),
					endTime = LocalDateTime.parse(it.endTime),
					visitorNumber = it.visitorNumber,
					pageStats = it.pageStats
				)
			}
		)
	}
}
