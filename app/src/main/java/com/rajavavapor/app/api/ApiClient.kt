package com.rajavavapor.app.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "https://poinraja.com/"

    @Volatile
    private var _service: ApiService? = null

    fun init(context: Context) {
        if (_service != null) return

        val client = buildClient(AuthInterceptor(context.applicationContext))

        _service = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val service: ApiService
        get() = _service ?: synchronized(this) {
            _service ?: createFallbackService().also { _service = it }
        }

    private fun createFallbackService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildClient(null))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun buildClient(authInterceptor: AuthInterceptor?): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })

        authInterceptor?.let { builder.addInterceptor(it) }
        return builder.build()
    }
}
