package com.rajavavapor.app.api

import android.content.Context
import android.content.Intent
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.ui.login.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Skip auth redirect for login endpoint
        val url = request.url.toString()
        if (url.contains("auth/login") || url.contains("face/verify")) {
            return response
        }

        if (response.code == 401) {
            val session = SessionManager(context)
            if (session.isLoggedIn()) {
                session.logout()
                val intent = Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            }
        }

        return response
    }
}
