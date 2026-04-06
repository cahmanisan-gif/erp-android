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

// ── Face Login ────────────────────────────────────────────────────────────────

data class FaceCabangResponse(
    val success: Boolean,
    val data: List<FaceCabang>?
)

data class FaceCabang(
    val id: Int,
    val kode: String?,
    val nama: String
)

data class FaceEmployeeResponse(
    val success: Boolean,
    val data: List<FaceEmployee>?
)

data class FaceEmployee(
    val id: Int,
    @SerializedName("nama_lengkap") val namaLengkap: String,
    val role: String?,
    @SerializedName("foto_url") val fotoUrl: String?,
    @SerializedName("personnel_id") val personnelId: String?
)

data class FaceVerifyResponse(
    val success: Boolean,
    val token: String?,
    val user: UserInfo?,
    val message: String?,
    val confidence: Double?
)

data class FaceRegisterResponse(
    val success: Boolean,
    val message: String?,
    @SerializedName("foto_url") val fotoUrl: String?
)

// ── Produk (POS) ─────────────────────────────────────────────────────────────

data class ProdukSearchResponse(val success: Boolean, val data: List<ProdukItem>?)

data class ProdukItem(
    val id: Int,
    @SerializedName("nama_produk") val namaProduk: String,
    @SerializedName("harga_jual") val hargaJual: Long,
    val stok: Int?,
    @SerializedName("kode_produk") val kodeProduk: String?
)

// ── Invoice ──────────────────────────────────────────────────────────────────

data class InvoiceResponse(val success: Boolean, val data: List<InvoiceItem>?)

data class InvoiceItem(
    val id: Int,
    @SerializedName("no_invoice") val noInvoice: String,
    val tanggal: String,
    val total: Long,
    val status: String?,
    @SerializedName("nama_customer") val namaCustomer: String?
)

// ── Customer ─────────────────────────────────────────────────────────────────

data class CustomerSearchResponse(val success: Boolean, val data: List<CustomerItem>?)

data class CustomerItem(
    val id: Int,
    val nama: String,
    @SerializedName("no_hp") val noHp: String?,
    val alamat: String?,
    @SerializedName("total_belanja") val totalBelanja: Long?
)

// ── Produk List ──────────────────────────────────────────────────────────────

data class ProdukListResponse(val success: Boolean, val data: List<ProdukListItem>?)

data class ProdukListItem(
    val id: Int,
    @SerializedName("nama_produk") val namaProduk: String,
    @SerializedName("harga_jual") val hargaJual: Long?,
    val stok: Int?,
    @SerializedName("kode_produk") val kodeProduk: String?,
    val kategori: String?
)

// ── Retur ─────────────────────────────────────────────────────────────────────

data class ReturResponse(val success: Boolean, val data: List<ReturItem>?)

data class ReturItem(
    val id: Int,
    @SerializedName("no_retur") val noRetur: String?,
    val tanggal: String?,
    val total: Long?,
    val status: String?,
    @SerializedName("nama_cabang") val namaCabang: String?
)

// ── Request Produk ───────────────────────────────────────────────────────────

data class RequestProdukResponse(val success: Boolean, val data: List<RequestProdukItem>?)

data class RequestProdukItem(
    val id: Int,
    val tanggal: String?,
    val status: String?,
    @SerializedName("nama_produk") val namaProduk: String?,
    val qty: Int?,
    @SerializedName("nama_cabang") val namaCabang: String?
)

// ── Kas / Bank ───────────────────────────────────────────────────────────────

data class KasResponse(val success: Boolean, val data: List<KasItem>?, val message: String?)

data class KasItem(
    val id: Int,
    val tanggal: String?,
    val keterangan: String?,
    val jenis: String?,
    val jumlah: Long,
    val saldo: Long?
)

// ── Piutang ──────────────────────────────────────────────────────────────────

data class PiutangResponse(val success: Boolean, val data: List<PiutangItem>?, val message: String?)

data class PiutangItem(
    val id: Int,
    @SerializedName("nama_customer") val namaCustomer: String?,
    val total: Long,
    val sisa: Long,
    @SerializedName("jatuh_tempo") val jatuhTempo: String?,
    val status: String?
)

// ── Pembelian ────────────────────────────────────────────────────────────────

data class PembelianResponse(val success: Boolean, val data: List<PembelianItem>?, val message: String?)

data class PembelianItem(
    val id: Int,
    @SerializedName("no_po") val noPo: String?,
    val tanggal: String?,
    val supplier: String?,
    val total: Long,
    val status: String?
)

// ── Payroll ──────────────────────────────────────────────────────────────────

data class PayrollResponse(val success: Boolean, val data: List<PayrollItem>?, val message: String?)

data class PayrollItem(
    val id: Int,
    @SerializedName("nama_karyawan") val namaKaryawan: String?,
    val bulan: Int,
    val tahun: Int,
    @SerializedName("gaji_pokok") val gajiPokok: Long?,
    @SerializedName("total_gaji") val totalGaji: Long,
    val status: String?
)

// ── Izin / Cuti ──────────────────────────────────────────────────────────────

data class IzinResponse(val success: Boolean, val data: List<IzinItem>?, val message: String?)

data class IzinItem(
    val id: Int,
    val tanggal: String?,
    val tipe: String?,
    val alasan: String?,
    val status: String?
)

// ── Monitoring Omzet ─────────────────────────────────────────────────────────

data class MonitoringOmzetResponse(val success: Boolean, val data: List<MonitoringOmzetItem>?, val message: String?)

data class MonitoringOmzetItem(
    val cabang: String,
    @SerializedName("omzet_hari_ini") val omzetHariIni: Double,
    @SerializedName("trx_hari_ini") val trxHariIni: Int,
    @SerializedName("omzet_bulan") val omzetBulan: Double
)

// ── Cabang ───────────────────────────────────────────────────────────────────

data class CabangListResponse(val success: Boolean, val data: List<CabangListItem>?, val message: String?)

data class CabangListItem(
    val id: Int,
    val nama: String,
    val kode: String?,
    val alamat: String?,
    @SerializedName("is_aktif") val isAktif: Boolean?
)

// ── Users ────────────────────────────────────────────────────────────────────

data class UsersResponse(val success: Boolean, val data: List<UserListItem>?, val message: String?)

data class UserListItem(
    val id: Int,
    val username: String,
    @SerializedName("nama_lengkap") val namaLengkap: String?,
    val role: String?,
    @SerializedName("nama_cabang") val namaCabang: String?,
    @SerializedName("is_aktif") val isAktif: Boolean?
)

// ── Promo ────────────────────────────────────────────────────────────────────

data class PromoResponse(val success: Boolean, val data: List<PromoItem>?, val message: String?)

data class PromoItem(
    val id: Int,
    val nama: String,
    val deskripsi: String?,
    @SerializedName("tanggal_mulai") val tanggalMulai: String?,
    @SerializedName("tanggal_selesai") val tanggalSelesai: String?,
    @SerializedName("is_aktif") val isAktif: Boolean?
)

// ── Generic ───────────────────────────────────────────────────────────────────

data class SimpleResponse(val success: Boolean, val message: String?)
