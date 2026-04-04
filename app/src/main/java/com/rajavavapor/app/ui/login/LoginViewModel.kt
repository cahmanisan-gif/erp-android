package com.rajavavapor.app.ui.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.LoginRequest
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    val isLoading = MutableLiveData(false)
    val loginSuccess = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    fun login(context: Context, username: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = ApiClient.service.login(LoginRequest(username, password))
                if (response.success && response.token != null && response.user != null) {
                    val session = SessionManager(context)
                    session.saveToken(response.token)
                    session.saveUser(response.user)
                    loginSuccess.value = true
                } else {
                    errorMessage.value = response.message ?: "Login gagal"
                }
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal. Periksa koneksi internet Anda."
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearError() { errorMessage.value = null }
}
