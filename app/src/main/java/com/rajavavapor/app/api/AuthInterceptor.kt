package com.rajavavapor.app.api

import android.content.Context
import android.content.Intent
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.ui.login.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Intercepts 401 Unauthorized responses and redirects to login.
 * This handles expired JWT tokens automatically.
 */
class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            // Token expired — clear session and redirect to login
            val session = SessionManager(context)
            session.logout()

            val intent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }

        return response
    }
}
