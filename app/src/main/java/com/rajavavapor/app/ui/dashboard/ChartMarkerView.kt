package com.rajavavapor.app.ui.dashboard

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.rajavavapor.app.R
import java.text.NumberFormat
import java.util.Locale

class ChartMarkerView(context: Context) : MarkerView(context, R.layout.chart_marker) {

    private val tvValue: TextView = findViewById(R.id.tvMarkerValue)
    private val tvLabel: TextView = findViewById(R.id.tvMarkerLabel)
    private var labels: List<String> = emptyList()

    fun setLabels(labels: List<String>) {
        this.labels = labels
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e == null) return
        val formatter = NumberFormat.getInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
        tvValue.text = "Rp ${formatter.format(e.y.toDouble())}"
        val idx = e.x.toInt()
        tvLabel.text = if (idx in labels.indices) labels[idx] else ""
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat() - 10f)
    }
}
