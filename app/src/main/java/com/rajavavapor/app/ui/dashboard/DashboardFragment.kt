package com.rajavavapor.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.LinearLayout
import android.widget.Space
import androidx.viewpager2.widget.ViewPager2
import com.rajavavapor.app.R
import com.rajavavapor.app.data.OwnerDashboardData
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.data.StatsData
import com.rajavavapor.app.data.TrendHari
import com.rajavavapor.app.databinding.FragmentDashboardBinding
import androidx.core.os.bundleOf
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

    private fun safeNavigate(destId: Int, args: Bundle? = null) {
        try { findNavController().navigate(destId, args) } catch (_: Exception) {}
    }

    private fun setupDashboardClicks() {
        binding.tvCash?.setOnClickListener { safeNavigate(R.id.navigation_kas) }
        binding.tvNonCash?.setOnClickListener { safeNavigate(R.id.navigation_kas) }
        binding.tvReturPending?.setOnClickListener { safeNavigate(R.id.navigation_retur) }
        binding.tvPengeluaran?.setOnClickListener { safeNavigate(R.id.navigation_kas) }
        binding.tvStaffHadir?.setOnClickListener { safeNavigate(R.id.navigation_absensi) }
        binding.tvStatRequest?.setOnClickListener { safeNavigate(R.id.navigation_request) }
        binding.tvStatInvoice?.setOnClickListener { safeNavigate(R.id.navigation_invoice) }
        binding.tvStatCustomer?.setOnClickListener { safeNavigate(R.id.navigation_customer) }
        binding.tvStatCabang?.setOnClickListener { safeNavigate(R.id.navigation_cabang) }
        binding.tvOmzetBulan?.setOnClickListener { safeNavigate(R.id.navigation_omzet_cabang) }
    }

    private fun showOwnerDashboard(data: OwnerDashboardData) {
        binding.layoutOwner.visibility = View.VISIBLE
        binding.layoutOwnerHeader?.visibility = View.VISIBLE
        binding.layoutBasicStats.visibility = View.GONE

        // Only animate hero number, rest set directly to reduce lag
        AnimationHelper.animateRupiah(binding.tvOmzetHariIni, data.omzetHariIni)
        binding.tvTrxHariIni.text = "${data.trxHariIni} transaksi"
        binding.tvOmzetBulan.text = data.omzetBulanIni.toRupiah()
        binding.tvTrxBulan.text = "${data.trxBulanIni} transaksi"

        val growth = data.growthVsKemarin
        if (growth != null) {
            val sign = if (growth >= 0) "▲" else "▼"
            val bgColor = if (growth >= 0) "#E8F5E9" else "#FDECEA"
            val textColor = if (growth >= 0) requireContext().getColor(R.color.success_green)
                        else requireContext().getColor(R.color.brand_red)
            binding.tvGrowth.text = "$sign $growth% vs kemarin"
            binding.tvGrowth.setTextColor(textColor)
            binding.tvGrowth.setBackgroundColor(Color.parseColor(bgColor))
            binding.tvGrowth.visibility = View.VISIBLE
        } else {
            binding.tvGrowth.visibility = View.GONE
        }

        binding.tvCash.text = data.cashHariIni.toRupiah()
        binding.tvNonCash.text = data.nonCashHariIni.toRupiah()
        binding.tvReturPending.text = data.returPending.toString()
        binding.tvPengeluaran.text = data.pengeluaranBulan.toRupiah()
        binding.tvStaffHadir.text = "${data.staffHadir}/${data.staffTotal}"

        // Top produk — clickable
        val topProduk = data.topProduk?.take(5) ?: emptyList()
        val produkText = topProduk.mapIndexed { i, p ->
            "${i + 1}. ${p.namaProduk ?: "-"}  •  ${p.totalQty} pcs"
        }.joinToString("\n").ifEmpty { "Belum ada data" }
        binding.tvTopProduk.text = produkText

        // Klik Top Produk card → detail produk terlaris per cabang
        binding.tvTopProduk.setOnClickListener {
            if (topProduk.isNotEmpty()) {
                val first = topProduk[0]
                safeNavigate(R.id.navigation_top_produk_detail, bundleOf(
                    "nama_produk" to (first.namaProduk ?: ""),
                    "total_qty" to first.totalQty,
                    "total_omzet" to first.totalOmzet
                ))
            }
        }

        // Top kasir
        val topKasir = data.topKasir?.take(5) ?: emptyList()
        val kasirText = topKasir.mapIndexed { i, k ->
            "${i + 1}. ${k.namaLengkap ?: "-"}  •  ${k.omzet.toRupiah()}"
        }.joinToString("\n").ifEmpty { "Belum ada data" }
        binding.tvTopKasir.text = kasirText

        // Klik Top Kasir → Leaderboard Kasir
        binding.tvTopKasir.setOnClickListener {
            safeNavigate(R.id.navigation_leaderboard, bundleOf("type" to "kasir"))
        }

        // Top cabang
        val topCabang = data.topCabang?.take(5) ?: emptyList()
        val cabangText = topCabang.mapIndexed { i, c ->
            "${i + 1}. ${c.nama ?: "-"}  •  ${c.omzet.toRupiah()}"
        }.joinToString("\n").ifEmpty { "Belum ada data" }
        binding.tvTopCabang.text = cabangText

        // Klik Top Cabang → Leaderboard Cabang
        binding.tvTopCabang.setOnClickListener {
            safeNavigate(R.id.navigation_leaderboard, bundleOf("type" to "cabang"))
        }

        // Charts ViewPager (swipe: Omzet, Keuntungan, Pengeluaran)
        setupChartPager(data.trend7Hari)
        setupPeriodTabs(data)
    }

    private fun setupPeriodTabs(data: OwnerDashboardData) {
        binding.chip7Hari?.setOnClickListener { setupChartPager(data.trend7Hari) }
        binding.chipBulan?.setOnClickListener { setupChartPager(data.trend7Hari) }
        binding.chipTahun?.setOnClickListener { setupChartPager(data.trend7Hari) }
    }

    private fun setupChartPager(trends: List<TrendHari>?) {
        val pager = binding.viewPagerCharts ?: return
        val dotsContainer = binding.dotsIndicator ?: return
        val pages = ChartPagerAdapter.buildPages(trends)

        if (pages.isEmpty()) {
            pager.visibility = View.GONE
            dotsContainer.visibility = View.GONE
            return
        }

        pager.visibility = View.VISIBLE
        dotsContainer.visibility = View.VISIBLE

        pager.adapter = ChartPagerAdapter(pages) { chart ->
            ChartMarkerView(requireContext()).also { it.chartView = chart }
        }

        // Dots indicator
        dotsContainer.removeAllViews()
        val dots = Array(pages.size) { createDot(it == 0) }
        dots.forEach { dot ->
            dotsContainer.addView(dot, LinearLayout.LayoutParams(8.dp, 8.dp).apply {
                marginStart = 4.dp; marginEnd = 4.dp
            })
        }

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dots.forEachIndexed { i, dot ->
                    val bg = dot.background as? GradientDrawable
                    if (i == position) {
                        bg?.setColor(Color.parseColor("#C1121F"))
                        dot.layoutParams = LinearLayout.LayoutParams(20.dp, 8.dp).apply {
                            marginStart = 4.dp; marginEnd = 4.dp
                        }
                    } else {
                        bg?.setColor(Color.parseColor("#DDDDDD"))
                        dot.layoutParams = LinearLayout.LayoutParams(8.dp, 8.dp).apply {
                            marginStart = 4.dp; marginEnd = 4.dp
                        }
                    }
                }
            }
        })
    }

    private fun createDot(active: Boolean): View {
        return View(requireContext()).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 4f.dp.toFloat()
                setColor(if (active) Color.parseColor("#C1121F") else Color.parseColor("#DDDDDD"))
            }
        }
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
    private val Float.dp: Int get() = (this * resources.displayMetrics.density).toInt()

    private fun showStatsDashboard(data: StatsData) {
        binding.layoutOwner.visibility = View.GONE
        binding.layoutBasicStats.visibility = View.VISIBLE

        binding.tvStatCabang.text = data.cabang.toString()
        binding.tvStatInvoice.text = data.invoice.toString()
        binding.tvStatCustomer.text = data.customer.toString()
        binding.tvStatRequest.text = data.request.toString()
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
