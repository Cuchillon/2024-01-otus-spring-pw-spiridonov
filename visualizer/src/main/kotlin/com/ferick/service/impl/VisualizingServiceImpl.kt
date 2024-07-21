package com.ferick.service.impl

import com.ferick.converters.AggregatedSiteDataConverter
import com.ferick.exceptions.NoSiteDataException
import com.ferick.exceptions.UnavailablePeriodException
import com.ferick.model.dto.PlotType
import com.ferick.repositories.AggregatedSiteDataRepository
import com.ferick.service.DataFrameService
import com.ferick.service.PlotService
import com.ferick.service.VisualizingService
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

@Service
class VisualizingServiceImpl(
    private val aggregatedSiteDataConverter: AggregatedSiteDataConverter,
    private val aggregatedSiteDataRepository: AggregatedSiteDataRepository,
    private val dataFrameService: DataFrameService,
    private val plotService: PlotService
) : VisualizingService {

    override fun getAvailablePeriod(): Pair<LocalDateTime, LocalDateTime> {
        if (aggregatedSiteDataRepository.count() < 1) {
            throw NoSiteDataException()
        }
        val startTime = aggregatedSiteDataRepository.findFirstByOrderByStartTimeAsc()!!.startTime
        val endTime = aggregatedSiteDataRepository.findFirstByOrderByStartTimeDesc()!!.startTime
        return startTime to endTime
    }

    override fun visualizeDataBetween(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): EnumMap<PlotType, List<Path>> {
        val paths = EnumMap<PlotType, List<Path>>(PlotType::class.java)
        checkStartTimeBetweenAvailablePeriod(startTime)
        checkEndTimeBetweenAvailablePeriod(endTime)
        val aggregatedSiteData = aggregatedSiteDataRepository.findByStartTimeBetween(startTime, endTime)
            .map { aggregatedSiteDataConverter.entityToDto(it) }
        val visualData = dataFrameService.getVisualData(aggregatedSiteData)
        val plots = plotService.getPlots(visualData)
        val visitorPath = plots[PlotType.PAGE_VISITORS_COUNT]?.get(PlotType.UNIQUE_KEY)?.save("visitors.png")!!
        paths[PlotType.PAGE_VISITORS_COUNT] = listOf(Paths.get(visitorPath))
        paths[PlotType.PAGE_VIEW_COUNT] = getPageViewPaths(plots[PlotType.PAGE_VIEW_COUNT]!!, "page_views_")
        paths[PlotType.PAGE_VIEW_PERIOD] = getPageViewPaths(plots[PlotType.PAGE_VIEW_PERIOD]!!, "page_view_period_")
        return paths
    }

    private fun getPageViewPaths(plots: Map<String, Plot>, prefix: String): List<Path> = plots.map { entry ->
        val pageName = (if (entry.key.startsWith("/")) entry.key.substring(1) else entry.key)
            .replace("/", "_")
        Paths.get(entry.value.save("$prefix$pageName.png"))
    }

    private fun checkStartTimeBetweenAvailablePeriod(startTime: LocalDateTime) {
        aggregatedSiteDataRepository.findFirstByOrderByStartTimeAsc()?.let {
            if (startTime.isBefore(it.startTime)) {
                throw UnavailablePeriodException("Available time starts from ${it.startTime}")
            }
        } ?: throw NoSiteDataException()
    }

    private fun checkEndTimeBetweenAvailablePeriod(endTime: LocalDateTime) {
        aggregatedSiteDataRepository.findFirstByOrderByStartTimeDesc()?.let {
            if (endTime.isAfter(it.startTime)) {
                throw UnavailablePeriodException("Available time ends with ${it.startTime}")
            }
        } ?: throw NoSiteDataException()
    }
}
