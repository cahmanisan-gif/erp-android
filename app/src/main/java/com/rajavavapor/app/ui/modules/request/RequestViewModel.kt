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

    private var allItems = listOf<RequestProdukItem>()
    val items = MutableLiveData<List<RequestProdukItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun filter(query: String) {
        if (query.length < 2) {
            items.value = allItems
            return
        }
        val q = query.lowercase()
        items.value = allItems.filter {
            (it.namaProduk?.lowercase()?.contains(q) == true) ||
            (it.namaCabang?.lowercase()?.contains(q) == true)
        }
    }

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = ApiClient.service.getRequestProduk(token)
                if (response.success) {
                    allItems = response.data ?: emptyList()
                    items.value = allItems
                }
                else errorMessage.value = "Gagal memuat data request produk"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
