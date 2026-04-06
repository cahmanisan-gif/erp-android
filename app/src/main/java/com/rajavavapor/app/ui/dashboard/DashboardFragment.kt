package com.rajavavapor.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.rajavavapor.app.data.OwnerDashboardData
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.data.StatsData
import com.rajavavapor.app.databinding.FragmentDashboardBinding
import androidx.navigation.fragment.findNavController
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
        binding.layoutBasicStats.visibility = View.GONE

        binding.tvOmzetHariIni.text = data.omzetHariIni.toRupiah()
        binding.tvTrxHariIni.text = "${data.trxHariIni} transaksi"
        binding.tvOmzetBulan.text = data.omzetBulanIni.toRupiah()
        binding.tvTrxBulan.text = "${data.trxBulanIni} transaksi"

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

        binding.tvCash.text = data.cashHariIni.toRupiah()
        binding.tvNonCash.text = data.nonCashHariIni.toRupiah()
        binding.tvReturPending.text = data.returPending.toString()
        binding.tvPengeluaran.text = data.pengeluaranBulan.toRupiah()
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
    }

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
