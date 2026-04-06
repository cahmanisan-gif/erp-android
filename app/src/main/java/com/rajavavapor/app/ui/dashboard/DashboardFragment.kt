package com.rajavavapor.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.rajavavapor.app.R
import com.rajavavapor.app.data.OwnerDashboardData
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.data.StatsData
import com.rajavavapor.app.data.TrendHari
import com.rajavavapor.app.databinding.FragmentDashboardBinding
import androidx.navigation.fragment.findNavController
import com.rajavavapor.app.util.AnimationHelper
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        val user = session.getUser()
        binding.tvGreeting.text = "Halo, ${user?.namaLengkap ?: user?.username ?: "User"}"
        binding.tvRole.text = formatRole(user?.role ?: "")
        binding.tvCabang.text = user?.namaCabang ?: ""

        binding.swipeRefresh.setColorSchemeResources(com.rajavavapor.app.R.color.brand_red)
        binding.swipeRefresh.setOnRefreshListener { viewModel.load(requireContext()) }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefresh.isRefreshing = loading
        }

        viewModel.ownerData.observe(viewLifecycleOwner) { data ->
            if (data != null) showOwnerDashboard(data)
        }

        viewModel.statsData.observe(viewLifecycleOwner) { data ->
            if (data != null) showStatsDashboard(data)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.load(requireContext())

        // Clickable dashboard cards
        setupDashboardClicks()
    }

    private fun setupDashboardClicks() {
        // Request Pending → Notifikasi tab
        binding.tvStatRequest?.setOnClickListener {
            findNavController().navigate(R.id.navigation_notifikasi)
        }
        // Retur Pending → Notifikasi tab
        binding.tvReturPending?.setOnClickListener {
            findNavController().navigate(R.id.navigation_notifikasi)
        }
        // Staff Hadir → Absensi tab
        binding.tvStaffHadir?.setOnClickListener {
            findNavController().navigate(R.id.navigation_absensi)
        }
        // Invoice → Member tab
        binding.tvStatInvoice?.setOnClickListener {
            findNavController().navigate(R.id.navigation_member)
        }
        // Customer → Member tab
        binding.tvStatCustomer?.setOnClickListener {
            findNavController().navigate(R.id.navigation_member)
        }
    }

    private fun showOwnerDashboard(data: OwnerDashboardData) {
        binding.layoutOwner.visibility = View.VISIBLE
        binding.layoutOwnerHeader?.visibility = View.VISIBLE
        binding.layoutBasicStats.visibility = View.GONE

        AnimationHelper.animateRupiah(binding.tvOmzetHariIni, data.omzetHariIni)
        AnimationHelper.animateCounterWithSuffix(binding.tvTrxHariIni, data.trxHariIni, "transaksi")
        AnimationHelper.animateRupiah(binding.tvOmzetBulan, data.omzetBulanIni)
        AnimationHelper.animateCounterWithSuffix(binding.tvTrxBulan, data.trxBulanIni, "transaksi")

        val growth = data.growthVsKemarin
        if (growth != null) {
            val sign = if (growth >= 0) "▲" else "▼"
            val color = if (growth >= 0) requireContext().getColor(com.rajavavapor.app.R.color.success_green)
                        else requireContext().getColor(com.rajavavapor.app.R.color.brand_red)
            binding.tvGrowth.text = "$sign $growth% vs kemarin"
            binding.tvGrowth.setTextColor(color)
            binding.tvGrowth.visibility = View.VISIBLE
        } else {
            binding.tvGrowth.visibility = View.GONE
        }

        AnimationHelper.animateRupiah(binding.tvCash, data.cashHariIni)
        AnimationHelper.animateRupiah(binding.tvNonCash, data.nonCashHariIni)
        AnimationHelper.animateCounter(binding.tvReturPending, data.returPending)
        AnimationHelper.animateRupiah(binding.tvPengeluaran, data.pengeluaranBulan)
        binding.tvStaffHadir.text = "${data.staffHadir}/${data.staffTotal}"

        // Top produk
        val topProduk = data.topProduk?.take(5) ?: emptyList()
        val produkText = topProduk.mapIndexed { i, p ->
            "${i + 1}. ${p.namaProduk ?: "-"}  •  ${p.totalQty} pcs"
        }.joinToString("\n").ifEmpty { "Belum ada data" }
        binding.tvTopProduk.text = produkText

        // Top kasir
        val topKasir = data.topKasir?.take(5) ?: emptyList()
        val kasirText = topKasir.mapIndexed { i, k ->
            "${i + 1}. ${k.namaLengkap ?: "-"}  •  ${k.omzet.toRupiah()}"
        }.joinToString("\n").ifEmpty { "Belum ada data" }
        binding.tvTopKasir.text = kasirText

        // Top cabang
        val topCabang = data.topCabang?.take(5) ?: emptyList()
        val cabangText = topCabang.mapIndexed { i, c ->
            "${i + 1}. ${c.nama ?: "-"}  •  ${c.omzet.toRupiah()}"
        }.joinToString("\n").ifEmpty { "Belum ada data" }
        binding.tvTopCabang.text = cabangText

        // Chart with period tabs
        setupChart(data.trend7Hari)
        setupChartTabs(data)
    }

    private var currentOwnerData: OwnerDashboardData? = null

    private fun setupChartTabs(data: OwnerDashboardData) {
        currentOwnerData = data
        binding.chip7Hari?.setOnClickListener {
            binding.tvChartTitle?.text = "Trend Omzet 7 Hari"
            setupChart(data.trend7Hari)
        }
        binding.chipBulan?.setOnClickListener {
            binding.tvChartTitle?.text = "Trend Omzet Bulan Ini"
            // Reuse 7-day data as monthly placeholder (API can provide monthly data later)
            setupChart(data.trend7Hari)
        }
        binding.chipTahun?.setOnClickListener {
            binding.tvChartTitle?.text = "Trend Omzet Tahun Ini"
            setupChart(data.trend7Hari)
        }
    }

    private fun setupChart(trends: List<TrendHari>?) {
        val chart = binding.chartTrend ?: return
        if (trends.isNullOrEmpty()) {
            chart.visibility = View.GONE
            return
        }
        chart.visibility = View.VISIBLE

        val entries = trends.mapIndexed { i, t -> Entry(i.toFloat(), t.omzet.toFloat()) }
        val labels = trends.map { it.tgl.takeLast(5) }

        val dataSet = LineDataSet(entries, "Omzet").apply {
            color = Color.parseColor("#C1121F")
            lineWidth = 2.5f
            setCircleColor(Color.parseColor("#C1121F"))
            circleRadius = 5f
            circleHoleRadius = 2.5f
            circleHoleColor = Color.WHITE
            setDrawValues(false)
            setDrawFilled(true)
            fillColor = Color.parseColor("#C1121F")
            fillAlpha = 25
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawHighlightIndicators(true)
            highLightColor = Color.parseColor("#C1121F")
            highlightLineWidth = 1f
            enableDashedHighlightLine(8f, 4f, 0f)
        }

        // Marker tooltip
        val marker = ChartMarkerView(requireContext())
        marker.setLabels(labels)
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
            animateX(800)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.parseColor("#999999")
                textSize = 10f
                valueFormatter = IndexAxisValueFormatter(labels)
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
            setExtraOffsets(8f, 16f, 8f, 8f)
            invalidate()
        }
    }

    private fun showStatsDashboard(data: StatsData) {
        binding.layoutOwner.visibility = View.GONE
        binding.layoutBasicStats.visibility = View.VISIBLE

        AnimationHelper.animateCounter(binding.tvStatCabang, data.cabang)
        AnimationHelper.animateCounter(binding.tvStatInvoice, data.invoice)
        AnimationHelper.animateCounter(binding.tvStatCustomer, data.customer)
        AnimationHelper.animateCounter(binding.tvStatRequest, data.request)
    }

    private fun Double.toRupiah(): String {
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return "Rp ${formatter.format(this)}"
    }

    private fun formatRole(role: String): String = when (role) {
        "owner" -> "Owner"
        "manajer" -> "Manajer"
        "manajer_area" -> "Manajer Area"
        "head_operational" -> "Head Operational"
        "admin_pusat" -> "Admin Pusat"
        "kasir" -> "Kasir"
        "kasir_sales" -> "Kasir Sales"
        "kepala_cabang" -> "Kepala Cabang"
        "vaporista" -> "Vaporista"
        else -> role.replace("_", " ").replaceFirstChar { it.uppercase() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
