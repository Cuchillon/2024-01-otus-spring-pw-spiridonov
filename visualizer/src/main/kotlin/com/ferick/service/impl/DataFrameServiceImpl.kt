package com.ferick.service.impl

import com.ferick.common.model.AggregatedSiteData
import com.ferick.common.model.PageStat
import com.ferick.model.PlotType
import com.ferick.model.VisualData
import com.ferick.service.DataFrameService
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@Service
class DataFrameServiceImpl : DataFrameService {

    override fun getVisualData(data: List<AggregatedSiteData>): VisualData {
        val dataFrames = EnumMap<PlotType, Map<String, AnyFrame>>(PlotType::class.java)
        val plotStartTime = formatDateTime(data[0].startTime)
        val plotEndTime = formatDateTime(data[data.size - 1].startTime)
        val minutes = data.indices.toList()
        val visitors = mutableListOf<Long>()
        val viewCounts = mutableMapOf<String, MutableList<Int>>()
        val viewPeriods = mutableMapOf<String, MutableList<Long>>()
        data.forEachIndexed { index, dataItem ->
            viewCounts.fillMissedPages(dataItem.pageStats) { it.add(DEFAULT_VALUE.toInt()) }
            viewPeriods.fillMissedPages(dataItem.pageStats) { it.add(DEFAULT_VALUE) }
            visitors.add(dataItem.visitorNumber)
            dataItem.pageStats.forEach { (page, stats) ->
                viewCounts.update(page, stats.viewCount, index, DEFAULT_VALUE.toInt())
                viewPeriods.update(page, stats.viewPeriod, index, DEFAULT_VALUE)
            }
        }
        dataFrames[PlotType.PAGE_VISITORS_COUNT] = mapOf(PlotType.UNIQUE_KEY to getVisitorDataFrame(minutes, visitors))
        dataFrames[PlotType.PAGE_VIEW_COUNT] = getViewCountDataFrames(minutes, viewCounts)
        dataFrames[PlotType.PAGE_VIEW_PERIOD] = getViewPeriodDataFrames(minutes, viewPeriods)
        return VisualData(plotStartTime, plotEndTime, dataFrames)
    }

    private fun getVisitorDataFrame(minutes: List<Int>, visitors: List<Long>): AnyFrame =
        dataFrameOf(
            "time" to minutes,
            "visitors" to visitors
        )

    private fun getViewCountDataFrames(
        minutes: List<Int>,
        viewCounts: Map<String, List<Int>>
    ): Map<String, AnyFrame> = viewCounts.mapValues { entry ->
        dataFrameOf(
            "time" to minutes,
            "viewCounts" to entry.value
        )
    }

    private fun getViewPeriodDataFrames(
        minutes: List<Int>,
        viewPeriods: Map<String, List<Long>>
    ): Map<String, AnyFrame> = viewPeriods.mapValues { entry ->
        dataFrameOf(
            "time" to minutes,
            "viewPeriods" to entry.value
        )
    }

    private fun <T> MutableMap<String, MutableList<T>>.update(page: String, element: T, index: Int, default: T) =
        this.compute(page) { _, count ->
            count?.apply {
                this.add(element)
            } ?: run {
                (0 until index).map { default }.toMutableList().also {
                    it.add(element)
                }
            }
        }

    private fun <T> MutableMap<String, MutableList<T>>.fillMissedPages(
        pageStats: Map<String, PageStat>,
        block: (MutableList<T>) -> Boolean
    ) = this.filterNot { entry ->
        pageStats.containsKey(entry.key)
    }.forEach { (_, stats) ->
        block.invoke(stats)
    }

    companion object {
        private const val DEFAULT_VALUE = 0L
        private val pattern = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)

        private fun formatDateTime(dateTime: String): String = LocalDateTime.parse(dateTime).format(pattern)
    }
}
