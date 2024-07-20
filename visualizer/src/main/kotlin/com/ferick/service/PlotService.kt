package com.ferick.service

import com.ferick.common.model.AggregatedSiteData
import com.ferick.model.PlotType
import org.jetbrains.kotlinx.kandy.ir.Plot
import java.util.*

interface PlotService {
    fun getPlots(data: List<AggregatedSiteData>): EnumMap<PlotType, Map<String, Plot>>
}
