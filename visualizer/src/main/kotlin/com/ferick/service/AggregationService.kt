package com.ferick.service

import com.ferick.common.model.AggregatedSiteData

interface AggregationService {
    fun saveAggregationSiteData(data: AggregatedSiteData)
}
