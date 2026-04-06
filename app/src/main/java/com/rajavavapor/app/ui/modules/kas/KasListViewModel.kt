package com.rajavavapor.app.ui.modules.kas

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.KasItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class KasListViewModel : ViewModel() {

    val items = MutableLiveData<List<KasItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = ApiClient.service.getKas(token)
                if (response.success) items.value = response.data ?: emptyList()
                else errorMessage.value = response.message ?: "Gagal memuat data kas"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
