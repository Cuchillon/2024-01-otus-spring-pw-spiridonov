package com.ferick.service.impl

import com.ferick.common.model.AggregatedSiteData
import com.ferick.common.model.PageStat
import com.ferick.model.PlotType
import com.ferick.service.PlotService
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.area
import org.jetbrains.kotlinx.kandy.letsplot.settings.LineType
import org.jetbrains.kotlinx.kandy.util.color.Color
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@Service
class PlotServiceImpl : PlotService {

    override fun getPlots(data: List<AggregatedSiteData>): EnumMap<PlotType, Map<String, Plot>> {
        val plots = EnumMap<PlotType, Map<String, Plot>>(PlotType::class.java)
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
        val visitorPlot = getVisitorPlot(minutes, visitors, plotStartTime, plotEndTime)
        plots[PlotType.PAGE_VISITORS_COUNT] = mapOf(PlotType.UNIQUE_KEY to visitorPlot)
        val viewCountPlots = getViewCountPlots(minutes, viewCounts, plotStartTime, plotEndTime)
        plots[PlotType.PAGE_VIEW_COUNT] = viewCountPlots
        val viewPeriodPlots = getViewPeriodPlots(minutes, viewPeriods, plotStartTime, plotEndTime)
        plots[PlotType.PAGE_VIEW_PERIOD] = viewPeriodPlots
        return plots
    }

    private fun getVisitorPlot(
        minutes: List<Int>,
        visitors: List<Long>,
        plotStartTime: String,
        plotEndTime: String
    ): Plot {
        val df = dataFrameOf(
            "time" to minutes,
            "visitors" to visitors
        )
        return df.plot {
            area {
                x("time") {
                    axis.name = "Time in minutes, period $plotStartTime - $plotEndTime"
                }
                y("visitors") {
                    axis.name = "Number of visitors"
                }
                borderLine.color = Color.ORANGE
                borderLine.type = LineType.BLANK
                borderLine.width = 1.5
                fillColor = Color.RED
                alpha = 0.7
            }
            layout.title = "Number of visitors"
            layout.size = 2000 to 500
        }
    }

    private fun getViewCountPlots(
        minutes: List<Int>,
        viewCounts: Map<String, List<Int>>,
        plotStartTime: String,
        plotEndTime: String
    ): Map<String, Plot> = viewCounts.mapValues { entry ->
        val df = dataFrameOf(
            "time" to minutes,
            "viewCounts" to entry.value
        )
        df.plot {
            area {
                x("time") {
                    axis.name = "Time in minutes, period $plotStartTime - $plotEndTime"
                }
                y("viewCounts") {
                    axis.name = "Number of page views"
                }
                borderLine.color = Color.ORANGE
                borderLine.type = LineType.BLANK
                borderLine.width = 1.5
                fillColor = Color.RED
                alpha = 0.7
            }
            layout.title = "Number of views for page ${entry.key}"
            layout.size = 1000 to 500
        }
    }

    private fun getViewPeriodPlots(
        minutes: List<Int>,
        viewPeriods: Map<String, List<Long>>,
        plotStartTime: String,
        plotEndTime: String
    ): Map<String, Plot> = viewPeriods.mapValues { entry ->
        val df = dataFrameOf(
            "time" to minutes,
            "viewPeriods" to entry.value
        )
        df.plot {
            area {
                x("time") {
                    axis.name = "Time in minutes, period $plotStartTime - $plotEndTime"
                }
                y("viewPeriods") {
                    axis.name = "Number of page view period"
                }
                borderLine.color = Color.ORANGE
                borderLine.type = LineType.BLANK
                borderLine.width = 1.5
                fillColor = Color.RED
                alpha = 0.7
            }
            layout.title = "Number of view period for page ${entry.key}"
            layout.size = 1000 to 500
        }
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
