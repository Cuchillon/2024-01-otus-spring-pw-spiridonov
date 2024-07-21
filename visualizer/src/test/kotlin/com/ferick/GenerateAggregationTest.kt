package com.ferick

import com.ferick.generator.AggregationGenerator
import com.ferick.model.PlotType
import com.ferick.service.PlotService
import com.ferick.service.impl.PlotServiceImpl
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.junit.jupiter.api.Test

class GenerateAggregationTest {

    private val plotService: PlotService = PlotServiceImpl()

    @Test
    fun generate() {
        val plots = plotService.getPlots(AggregationGenerator.generate())
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
