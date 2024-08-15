package com.ferick.model.dto

import org.jetbrains.kotlinx.dataframe.AnyFrame
import java.util.EnumMap

data class VisualData(
    val plotStartTime: String,
    val plotEndTime: String,
    val dataFrames: EnumMap<PlotType, Map<String, AnyFrame>>
)
