package com.rajavavapor.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("raja_vapor_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveToken(token: String) = prefs.edit().putString("token", token).apply()

    fun getToken(): String? = prefs.getString("token", null)

    fun bearerToken(): String = "Bearer ${getToken() ?: ""}"

    fun saveUser(user: UserInfo) = prefs.edit().putString("user", gson.toJson(user)).apply()

    fun getUser(): UserInfo? {
        val json = prefs.getString("user", null) ?: return null
        return try { gson.fromJson(json, UserInfo::class.java) } catch (e: Exception) { null }
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun logout() = prefs.edit().clear().apply()
}
