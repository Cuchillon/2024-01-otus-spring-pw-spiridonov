package com.ferick.service.impl

import com.ferick.converters.AggregatedSiteDataConverter
import com.ferick.exceptions.NoSiteDataException
import com.ferick.exceptions.UnavailablePeriodException
import com.ferick.model.dto.PeriodData
import com.ferick.model.dto.PeriodRequest
import com.ferick.model.dto.PlotType
import com.ferick.repositories.AggregatedSiteDataRepository
import com.ferick.service.DataFrameService
import com.ferick.service.PlotService
import com.ferick.service.VisualizingService
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.springframework.stereotype.Service
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

    override fun visualizeDataBetween(request: PeriodRequest): PeriodData {
        val startTime = LocalDateTime.parse(request.startTime)
        val endTime = LocalDateTime.parse(request.endTime)
        val imageNames = EnumMap<PlotType, List<String>>(PlotType::class.java)
        checkStartTimeBetweenAvailablePeriod(startTime)
        checkEndTimeBetweenAvailablePeriod(endTime)
        val aggregatedSiteData = aggregatedSiteDataRepository.findByStartTimeBetween(startTime, endTime)
            .map { aggregatedSiteDataConverter.entityToDto(it) }
        val visualData = dataFrameService.getVisualData(aggregatedSiteData)
        val plots = plotService.getPlots(visualData)
        plots[PlotType.PAGE_VISITORS_COUNT]?.get(PlotType.UNIQUE_KEY)?.save(VISITORS_FILE_NAME)!!
        imageNames[PlotType.PAGE_VISITORS_COUNT] = listOf(VISITORS_FILE_NAME)
        imageNames[PlotType.PAGE_VIEW_COUNT] =
            getPageViewNames(plots[PlotType.PAGE_VIEW_COUNT]!!, "page_views_")
        imageNames[PlotType.PAGE_VIEW_PERIOD] =
            getPageViewNames(plots[PlotType.PAGE_VIEW_PERIOD]!!, "page_view_period_")
        return PeriodData(startTime, endTime, imageNames)
    }

    private fun getPageViewNames(plots: Map<String, Plot>, prefix: String): List<String> = plots.map { entry ->
        val pageName = (if (entry.key.startsWith("/")) entry.key.substring(1) else entry.key)
            .replace("/", "_")
        val imageFileName = "$prefix$pageName.png"
        entry.value.save(imageFileName)
        imageFileName
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

    companion object {
        private const val VISITORS_FILE_NAME = "visitors.png"
    }
}
