package com.rajavavapor.app.ui.modules.piutang

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.PiutangItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class PiutangViewModel : ViewModel() {

    val items = MutableLiveData<List<PiutangItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = ApiClient.service.getPiutang(token)
                if (response.success) items.value = response.data ?: emptyList()
                else errorMessage.value = response.message ?: "Gagal memuat data piutang"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
