package com.ferick.service

import com.ferick.common.model.AggregatedSiteData
import com.ferick.model.dto.VisualData

interface DataFrameService {
    fun getVisualData(data: List<AggregatedSiteData>): VisualData
}
