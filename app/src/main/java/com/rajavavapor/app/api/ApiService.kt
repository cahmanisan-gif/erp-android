package com.rajavavapor.app.api

import com.rajavavapor.app.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/dashboard/stats")
    suspend fun getDashboardStats(
        @Header("Authorization") token: String
    ): StatsResponse

    @GET("api/dashboard/owner")
    suspend fun getDashboardOwner(
        @Header("Authorization") token: String
    ): OwnerDashboardResponse

    @GET("api/notifikasi")
    suspend fun getNotifikasi(
        @Header("Authorization") token: String
    ): NotifikasiResponse

    @GET("api/notifikasi/unread-count")
    suspend fun getUnreadCount(
        @Header("Authorization") token: String
    ): UnreadCountResponse

    @POST("api/notifikasi/baca/{id}")
    suspend fun bacaNotifikasi(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): SimpleResponse

    @POST("api/notifikasi/baca-semua")
    suspend fun bacaSemua(
        @Header("Authorization") token: String
    ): SimpleResponse

    @GET("api/member/search")
    suspend fun searchMember(
        @Header("Authorization") token: String,
        @Query("q") query: String
    ): MemberSearchResponse

    @GET("api/ping")
    suspend fun ping(): SimpleResponse

    // ── Absensi (base: /absensi/api/) ──────────────────────────────────────

    @Multipart
    @POST("absensi/api/clock")
    suspend fun clockAbsensi(
        @Header("Authorization") token: String,
        @Part foto: MultipartBody.Part,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part("accuracy") accuracy: RequestBody,
        @Part("tipe") tipe: RequestBody,
        @Part("cabang_id") cabangId: RequestBody,
        @Part("barcode") barcode: RequestBody? = null
    ): AbsensiClockResponse

    @GET("absensi/api/status")
    suspend fun getAbsensiStatus(
        @Header("Authorization") token: String
    ): AbsensiStatusResponse

    @GET("absensi/api/riwayat")
    suspend fun getAbsensiRiwayat(
        @Header("Authorization") token: String,
        @Query("bulan") bulan: Int? = null,
        @Query("tahun") tahun: Int? = null
    ): AbsensiRiwayatResponse

    // ── Face Login ───────────────────────────────────────────────────────────

    @GET("absensi/api/face/employees")
    suspend fun getFaceEmployees(
        @Query("cabang_id") cabangId: Int
    ): FaceEmployeeResponse

    @Multipart
    @POST("absensi/api/face/verify")
    suspend fun verifyFace(
        @Part foto: MultipartBody.Part,
        @Part("personnel_id") personnelId: RequestBody
    ): FaceVerifyResponse
}
