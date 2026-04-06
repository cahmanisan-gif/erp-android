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

    private var allItems = listOf<PembelianItem>()
    val items = MutableLiveData<List<PembelianItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun filter(query: String) {
        if (query.length < 2) {
            items.value = allItems
            return
        }
        val q = query.lowercase()
        items.value = allItems.filter {
            (it.noPo?.lowercase()?.contains(q) == true) ||
            (it.supplier?.lowercase()?.contains(q) == true)
        }
    }

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = ApiClient.service.getPembelian(token)
                if (response.success) {
                    allItems = response.data ?: emptyList()
                    items.value = allItems
                }
                else errorMessage.value = response.message ?: "Gagal memuat data pembelian"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
