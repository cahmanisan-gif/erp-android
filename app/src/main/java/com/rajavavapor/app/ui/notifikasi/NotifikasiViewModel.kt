package com.rajavavapor.app.ui.notifikasi

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.NotifikasiItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class NotifikasiViewModel : ViewModel() {

    val items = MutableLiveData<List<NotifikasiItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = ApiClient.service.getNotifikasi(token)
                if (response.success) items.value = response.data ?: emptyList()
                else errorMessage.value = "Gagal memuat notifikasi"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun bacaNotifikasi(context: Context, id: Int) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            try {
                ApiClient.service.bacaNotifikasi(token, id)
                // Update local state - mark as read
                items.value = items.value?.map {
                    if (it.id == id) it.copy(dibaca = 1) else it
                }
            } catch (_: Exception) {}
        }
    }

    fun bacaSemua(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            try {
                ApiClient.service.bacaSemua(token)
                items.value = items.value?.map { it.copy(dibaca = 1) }
            } catch (_: Exception) {}
        }
    }
}
