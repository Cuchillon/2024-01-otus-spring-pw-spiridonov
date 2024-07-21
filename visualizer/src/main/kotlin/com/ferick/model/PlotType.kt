package com.ferick.model

enum class PlotType {
    PAGE_VISITORS_COUNT,
    PAGE_VIEW_COUNT,
    PAGE_VIEW_PERIOD;

    companion object {
        const val UNIQUE_KEY = "unique"
    }
}
