package com.rajavavapor.app.ui.menu

import com.rajavavapor.app.R

data class MenuItem(
    val id: String,
    val title: String,
    val icon: Int,
    val color: Int,
    val minRoleLevel: Int,
    val navDestination: Int? = null
)

object MenuConfig {

    // Role levels matching backend hierarchy
    fun getRoleLevel(role: String): Int = when (role) {
        "owner" -> 7
        "manajer" -> 6
        "head_operational" -> 6
        "admin_pusat" -> 5
        "spv_area" -> 4
        "finance" -> 4
        "kepala_cabang" -> 3
        "sales" -> 2
        "kasir_sales" -> 2
        "kasir" -> 1
        "vaporista" -> 0
        else -> 0
    }

    fun getMenuItems(): List<MenuItem> = listOf(
        // ── Utama ──
        MenuItem("dashboard", "Dashboard", R.drawable.ic_menu_dashboard, R.color.brand_red, 0, R.id.navigation_dashboard),
        MenuItem("absensi", "Absensi", R.drawable.ic_menu_absensi, R.color.success_green, 0, R.id.navigation_absensi),
        MenuItem("notifikasi", "Notifikasi", R.drawable.ic_menu_notifikasi, R.color.brand_red, 0, R.id.navigation_notifikasi),

        // ── Penjualan ──
        MenuItem("pos", "POS / Kasir", R.drawable.ic_menu_pos, R.color.brand_red, 1, R.id.navigation_pos),
        MenuItem("invoice", "Invoice", R.drawable.ic_menu_invoice, R.color.dark_bg, 1, R.id.navigation_invoice),
        MenuItem("member", "Member", R.drawable.ic_menu_member, R.color.brand_dark, 1, R.id.navigation_member),
        MenuItem("customer", "Customer", R.drawable.ic_menu_customer, R.color.success_green, 1, R.id.navigation_customer),

        // ── Stok & Produk ──
        MenuItem("produk", "Produk", R.drawable.ic_menu_produk, R.color.dark_bg, 2, R.id.navigation_produk),
        MenuItem("retur", "Retur", R.drawable.ic_menu_retur, R.color.brand_red, 2, R.id.navigation_retur),
        MenuItem("request", "Request Produk", R.drawable.ic_menu_request, R.color.brand_dark, 1, R.id.navigation_request),

        // ── Keuangan ──
        MenuItem("kas", "Kas / Bank", R.drawable.ic_menu_kas, R.color.success_green, 3, R.id.navigation_kas),
        MenuItem("piutang", "Piutang", R.drawable.ic_menu_piutang, R.color.brand_red, 3, R.id.navigation_piutang),
        MenuItem("pembelian", "Pembelian", R.drawable.ic_menu_pembelian, R.color.dark_bg, 3, R.id.navigation_pembelian),

        // ── SDM ──
        MenuItem("payroll", "Payroll", R.drawable.ic_menu_payroll, R.color.brand_dark, 4, R.id.navigation_payroll),
        MenuItem("izin", "Izin / Cuti", R.drawable.ic_menu_izin, R.color.success_green, 0, R.id.navigation_izin),

        // ── Monitoring & Laporan ──
        MenuItem("monitoring", "Monitoring", R.drawable.ic_menu_monitoring, R.color.brand_red, 3, R.id.navigation_monitoring),
        MenuItem("laporan", "Laporan Keuangan", R.drawable.ic_menu_laporan, R.color.dark_bg, 4, R.id.navigation_laporan),

        // ── Pengaturan (management only) ──
        MenuItem("cabang", "Cabang", R.drawable.ic_menu_cabang, R.color.success_green, 5, R.id.navigation_cabang),
        MenuItem("users", "User Management", R.drawable.ic_menu_users, R.color.dark_bg, 5, R.id.navigation_users),
        MenuItem("promo", "Promo", R.drawable.ic_menu_promo, R.color.brand_red, 3, R.id.navigation_promo),
    )

    fun getMenuForRole(role: String): List<MenuItem> {
        val level = getRoleLevel(role)
        return getMenuItems().filter { it.minRoleLevel <= level }
    }
}
