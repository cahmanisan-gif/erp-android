package com.rajavavapor.app.api

import com.rajavavapor.app.data.*
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
}
