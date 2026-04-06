package com.rajavavapor.app.ui.modules.pembelian

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.PembelianItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class PembelianViewModel : ViewModel() {

    val items = MutableLiveData<List<PembelianItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = ApiClient.service.getPembelian(token)
                if (response.success) items.value = response.data ?: emptyList()
                else errorMessage.value = response.message ?: "Gagal memuat data pembelian"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
