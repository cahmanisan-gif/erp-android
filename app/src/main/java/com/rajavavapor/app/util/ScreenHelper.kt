package com.rajavavapor.app.util

import android.content.Context

object ScreenHelper {

    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.smallestScreenWidthDp >= 600
    }

    fun getGridColumns(context: Context): Int {
        return if (isTablet(context)) 2 else 1
    }
}
