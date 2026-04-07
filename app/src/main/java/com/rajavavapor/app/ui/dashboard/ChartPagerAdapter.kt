package com.rajavavapor.app.ui.dashboard

import android.animation.ValueAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.rajavavapor.app.data.TrendHari
import com.rajavavapor.app.databinding.ItemChartPageBinding
import java.text.NumberFormat
import java.util.Locale

data class ChartPageData(
    val title: String,
    val color: Int,
    val totalValue: Double,
    val entries: List<Entry>,
    val labels: List<String>
)

class ChartPagerAdapter(
    private val pages: List<ChartPageData>,
    private val markerFactory: (LineChart) -> ChartMarkerView
) : RecyclerView.Adapter<ChartPagerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemChartPageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChartPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val page = pages[position]
        val formatter = NumberFormat.getInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }

        holder.binding.tvChartTitle.text = page.title
        holder.binding.tvChartValue.text = "Rp ${formatter.format(page.totalValue)}"
        holder.binding.tvChartValue.setTextColor(page.color)

        setupLineChart(holder.binding.lineChart, page)
    }

    override fun getItemCount() = pages.size

    private fun setupLineChart(chart: LineChart, page: ChartPageData) {
        val dataSet = LineDataSet(page.entries, page.title).apply {
            color = page.color
            lineWidth = 2.5f
            setCircleColor(page.color)
            circleRadius = 4.5f
            circleHoleRadius = 2f
            circleHoleColor = Color.WHITE
            setDrawValues(false)
            setDrawFilled(true)
            fillColor = page.color
            fillAlpha = 0 // Start at 0 for animation
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawHighlightIndicators(true)
            highLightColor = page.color
            highlightLineWidth = 1f
            enableDashedHighlightLine(8f, 4f, 0f)
        }

        val marker = markerFactory(chart)
        marker.setLabels(page.labels)
        marker.chartView = chart

        chart.apply {
            data = LineData(dataSet)
            this.marker = marker
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setScaleEnabled(false)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false

            xAxis.apply {
                this.position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.parseColor("#999999")
                textSize = 10f
                valueFormatter = IndexAxisValueFormatter(page.labels)
                yOffset = 8f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#F0F0F0")
                gridLineWidth = 0.5f
                textColor = Color.parseColor("#999999")
                textSize = 9f
                setDrawAxisLine(false)
            }

            axisRight.isEnabled = false
            setExtraOffsets(8f, 8f, 8f, 8f)

            // Light animation — no fill animator to reduce lag
            dataSet.fillAlpha = 25
            animateX(400)
            invalidate()
        }
    }

    companion object {
        fun buildPages(trends: List<TrendHari>?): List<ChartPageData> {
            if (trends.isNullOrEmpty()) return emptyList()

            val labels = trends.map { it.tgl.takeLast(5) }

            val omzetEntries = trends.mapIndexed { i, t -> Entry(i.toFloat(), t.omzet.toFloat()) }
            val keuntunganEntries = trends.mapIndexed { i, t -> Entry(i.toFloat(), t.keuntungan.toFloat()) }
            val hppEntries = trends.mapIndexed { i, t -> Entry(i.toFloat(), t.hpp.toFloat()) }

            return listOf(
                ChartPageData(
                    title = "Omzet",
                    color = Color.parseColor("#C1121F"),
                    totalValue = trends.sumOf { it.omzet },
                    entries = omzetEntries,
                    labels = labels
                ),
                ChartPageData(
                    title = "Keuntungan",
                    color = Color.parseColor("#2E7D32"),
                    totalValue = trends.sumOf { it.keuntungan },
                    entries = keuntunganEntries,
                    labels = labels
                ),
                ChartPageData(
                    title = "HPP / Pengeluaran",
                    color = Color.parseColor("#1565C0"),
                    totalValue = trends.sumOf { it.hpp },
                    entries = hppEntries,
                    labels = labels
                )
            )
        }
    }
}
