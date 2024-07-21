package com.ferick.service

import com.ferick.model.PlotType
import com.ferick.model.VisualData
import org.jetbrains.kotlinx.kandy.ir.Plot
import java.util.*

interface PlotService {
    fun getPlots(data: VisualData): EnumMap<PlotType, Map<String, Plot>>
}
