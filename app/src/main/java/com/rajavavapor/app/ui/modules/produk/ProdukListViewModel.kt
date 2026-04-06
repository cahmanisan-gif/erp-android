package com.rajavavapor.app.ui.modules.produk

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.ProdukListItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class ProdukListViewModel : ViewModel() {

    val items = MutableLiveData<List<ProdukListItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    private var currentSearch: String? = null

    fun load(context: Context, search: String? = currentSearch) {
        currentSearch = search
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = ApiClient.service.getProduk(token, page = 1, search = search)
                if (response.success) items.value = response.data ?: emptyList()
                else errorMessage.value = "Gagal memuat produk"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
