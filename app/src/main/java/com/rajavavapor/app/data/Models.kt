package com.rajavavapor.app.data

import com.google.gson.annotations.SerializedName

// ── Auth ──────────────────────────────────────────────────────────────────────

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val user: UserInfo?,
    val message: String?
)

data class UserInfo(
    val id: Int,
    val username: String,
    @SerializedName("nama_lengkap") val namaLengkap: String?,
    val role: String,
    @SerializedName("cabang_id") val cabangId: Int?,
    @SerializedName("nama_cabang") val namaCabang: String?,
    @SerializedName("personnel_id") val personnelId: String?
)

// ── Dashboard Stats (semua role) ──────────────────────────────────────────────

data class StatsResponse(val success: Boolean, val data: StatsData?, val message: String?)

data class StatsData(
    val cabang: Int,
    val invoice: Int,
    val customer: Int,
    val request: Int
)

// ── Dashboard Owner ───────────────────────────────────────────────────────────

data class OwnerDashboardResponse(val success: Boolean, val data: OwnerDashboardData?, val message: String?)

data class OwnerDashboardData(
    @SerializedName("omzet_hari_ini") val omzetHariIni: Double,
    @SerializedName("trx_hari_ini") val trxHariIni: Int,
    @SerializedName("cash_hari_ini") val cashHariIni: Double,
    @SerializedName("non_cash_hari_ini") val nonCashHariIni: Double,
    @SerializedName("omzet_kemarin") val omzetKemarin: Double,
    @SerializedName("omzet_bulan_ini") val omzetBulanIni: Double,
    @SerializedName("trx_bulan_ini") val trxBulanIni: Int,
    @SerializedName("growth_vs_kemarin") val growthVsKemarin: Int?,
    @SerializedName("trend_7_hari") val trend7Hari: List<TrendHari>?,
    @SerializedName("top_cabang") val topCabang: List<TopCabang>?,
    @SerializedName("top_produk") val topProduk: List<TopProduk>?,
    @SerializedName("top_kasir") val topKasir: List<TopKasir>?,
    @SerializedName("retur_pending") val returPending: Int,
    @SerializedName("pengeluaran_bulan") val pengeluaranBulan: Double,
    @SerializedName("staff_hadir") val staffHadir: Int,
    @SerializedName("staff_total") val staffTotal: Int
)

data class TrendHari(
    val tgl: String,
    val omzet: Double,
    val trx: Int,
    val hpp: Double,
    val keuntungan: Double
)

data class TopCabang(
    val nama: String?,
    val kode: String?,
    val omzet: Double,
    val trx: Int
)

data class TopProduk(
    @SerializedName("nama_produk") val namaProduk: String?,
    @SerializedName("total_qty") val totalQty: Int,
    @SerializedName("total_omzet") val totalOmzet: Double
)

data class TopKasir(
    @SerializedName("nama_lengkap") val namaLengkap: String?,
    @SerializedName("nama_cabang") val namaCabang: String?,
    val omzet: Double,
    val trx: Int
)

// ── Notifikasi ────────────────────────────────────────────────────────────────

data class NotifikasiResponse(val success: Boolean, val data: List<NotifikasiItem>?)

data class UnreadCountResponse(val success: Boolean, val count: Int)

data class NotifikasiItem(
    val id: Int,
    val tipe: String?,
    val judul: String,
    val pesan: String?,
    val link: String?,
    val dibaca: Int,
    @SerializedName("created_at") val createdAt: String
)

// ── Member ────────────────────────────────────────────────────────────────────

data class MemberSearchResponse(val success: Boolean, val data: List<MemberItem>?)

data class MemberItem(
    val id: Int,
    @SerializedName("no_hp") val noHp: String?,
    val nama: String,
    val tier: String?,
    @SerializedName("total_poin") val totalPoin: Long?,
    @SerializedName("total_belanja") val totalBelanja: Long?,
    @SerializedName("total_transaksi") val totalTransaksi: Int?
)

// ── Absensi ──────────────────────────────────────────────────────────────────

data class AbsensiClockResponse(
    val success: Boolean,
    val message: String?,
    val data: AbsensiClockData?
)

data class AbsensiClockData(
    val waktu: String?,
    val jarak: Double?,
    val valid: Boolean?
)

data class AbsensiStatusResponse(
    val success: Boolean,
    val data: AbsensiStatusData?
)

data class AbsensiStatusData(
    val status: String?,
    @SerializedName("clock_in") val clockIn: String?,
    @SerializedName("clock_out") val clockOut: String?,
    val izin: String?,
    val cabang: String?,
    val tanggal: String?
)

data class AbsensiRiwayatResponse(
    val success: Boolean,
    val data: List<AbsensiRiwayatItem>?,
    val summary: AbsensiSummary?
)

data class AbsensiRiwayatItem(
    val tanggal: String,
    val masuk: String?,
    val pulang: String?
)

data class AbsensiSummary(
    val hadir: Int?
)

// ── Generic ───────────────────────────────────────────────────────────────────

data class SimpleResponse(val success: Boolean, val message: String?)
