package com.rajavavapor.app.ui.modules.request

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.RequestProdukItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class RequestViewModel : ViewModel() {

    val items = MutableLiveData<List<RequestProdukItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = ApiClient.service.getRequestProduk(token)
                if (response.success) items.value = response.data ?: emptyList()
                else errorMessage.value = "Gagal memuat data request produk"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
