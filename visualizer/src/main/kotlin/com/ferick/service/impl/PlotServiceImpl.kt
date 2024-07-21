package com.ferick.service.impl

import com.ferick.model.PlotType
import com.ferick.model.VisualData
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
class PlotServiceImpl : PlotService {

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
            layout.title = "Number of visitors"
            layout.size = 2000 to 500
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
            layout.title = "Number of views for page ${entry.key}"
            layout.size = 1000 to 500
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
            layout.title = "Number of view period for page ${entry.key}"
            layout.size = 1000 to 500
        }
    }
}
