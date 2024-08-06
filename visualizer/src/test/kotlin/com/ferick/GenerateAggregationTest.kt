package com.ferick

import com.ferick.configuration.properties.Layout
import com.ferick.configuration.properties.PlotProperties
import com.ferick.generator.AggregationGenerator
import com.ferick.model.dto.PlotType
import com.ferick.service.DataFrameService
import com.ferick.service.PlotService
import com.ferick.service.impl.DataFrameServiceImpl
import com.ferick.service.impl.PlotServiceImpl
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.junit.jupiter.api.Test

class GenerateAggregationTest {

    private val dataFrameService: DataFrameService = DataFrameServiceImpl()
    private val plotService: PlotService = PlotServiceImpl(
        PlotProperties(
            visitors = Layout("Number of visitors", 2000, 500),
            viewCounts = Layout("Number of views for page", 1000, 500),
            viewPeriods = Layout("Number of view period for page", 1000, 500)
        )
    )

    @Test
    fun generate() {
        val visualData = dataFrameService.getVisualData(AggregationGenerator.generate())
        val plots = plotService.getPlots(visualData)
        plots[PlotType.PAGE_VISITORS_COUNT]?.get(PlotType.UNIQUE_KEY)?.save("visitors.png")
        plots[PlotType.PAGE_VIEW_COUNT]?.forEach { (page, plot) ->
            val pageName = page.substring(1)
            plot.save("page_views_$pageName.png")
        }
        plots[PlotType.PAGE_VIEW_PERIOD]?.forEach { (page, plot) ->
            val pageName = page.substring(1)
            plot.save("page_view_period_$pageName.png")
        }
    }
}
