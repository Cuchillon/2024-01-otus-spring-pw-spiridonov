package com.ferick.service.impl

import com.ferick.configuration.properties.PlotProperties
import com.ferick.model.dto.PlotType
import com.ferick.model.dto.VisualData
import com.ferick.service.PlotService
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.area
import org.jetbrains.kotlinx.kandy.letsplot.settings.LineType
import org.jetbrains.kotlinx.kandy.util.color.Color
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlotServiceImpl(
    private val plotProperties: PlotProperties
) : PlotService {

    override fun getPlots(data: VisualData): EnumMap<PlotType, Map<String, Plot>> {
        val plots = EnumMap<PlotType, Map<String, Plot>>(PlotType::class.java)
        val visitorPlot = getVisitorPlot(
            data.dataFrames[PlotType.PAGE_VISITORS_COUNT]!![PlotType.UNIQUE_KEY]!!,
            data.plotStartTime,
            data.plotEndTime
        )
        plots[PlotType.PAGE_VISITORS_COUNT] = mapOf(PlotType.UNIQUE_KEY to visitorPlot)
        val viewCountPlots = getViewCountPlots(
            data.dataFrames[PlotType.PAGE_VIEW_COUNT]!!,
            data.plotStartTime,
            data.plotEndTime
        )
        plots[PlotType.PAGE_VIEW_COUNT] = viewCountPlots
        val viewPeriodPlots = getViewPeriodPlots(
            data.dataFrames[PlotType.PAGE_VIEW_PERIOD]!!,
            data.plotStartTime,
            data.plotEndTime
        )
        plots[PlotType.PAGE_VIEW_PERIOD] = viewPeriodPlots
        return plots
    }

    private fun getVisitorPlot(
        dataFrame: AnyFrame,
        plotStartTime: String,
        plotEndTime: String
    ): Plot {
        return dataFrame.plot {
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
            layout.title = plotProperties.visitors.title
            layout.size = plotProperties.visitors.width to plotProperties.visitors.height
        }
    }

    private fun getViewCountPlots(
        dataFrames: Map<String, AnyFrame>,
        plotStartTime: String,
        plotEndTime: String
    ): Map<String, Plot> = dataFrames.mapValues { entry ->
        entry.value.plot {
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
            layout.title = "${plotProperties.viewCounts.title} ${entry.key}"
            layout.size = plotProperties.viewCounts.width to plotProperties.viewCounts.height
        }
    }

    private fun getViewPeriodPlots(
        dataFrames: Map<String, AnyFrame>,
        plotStartTime: String,
        plotEndTime: String
    ): Map<String, Plot> = dataFrames.mapValues { entry ->
        entry.value.plot {
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
            layout.title = "${plotProperties.viewPeriods.title} ${entry.key}"
            layout.size = plotProperties.viewPeriods.width to plotProperties.viewPeriods.height
        }
    }
}
