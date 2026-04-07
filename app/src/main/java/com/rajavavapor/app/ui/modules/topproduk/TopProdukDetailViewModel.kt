package com.rajavavapor.app.ui.modules.topproduk

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.ProdukCabangItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class TopProdukDetailViewModel : ViewModel() {

    val items = MutableLiveData<List<ProdukCabangItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context, namaProduk: String) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = withTimeoutOrNull(5000) {
                    ApiClient.service.getTopProdukDetail(token, namaProduk)
                }
                if (response?.success == true && !response.data.isNullOrEmpty()) {
                    items.value = response.data.sortedByDescending { it.qty }
                } else {
                    items.value = emptyList()
                    errorMessage.value = "Detail per cabang belum tersedia untuk produk ini"
                }
            } catch (e: Exception) {
                items.value = emptyList()
                errorMessage.value = "Detail per cabang belum tersedia"
            } finally {
                isLoading.value = false
            }
        }
    }
}
