package com.rajavavapor.app.ui.modules.topproduk

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.ProdukCabangItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class TopProdukDetailViewModel : ViewModel() {

    val items = MutableLiveData<List<ProdukCabangItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context, namaProduk: String) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = ApiClient.service.getTopProdukDetail(token, namaProduk)
                if (response.success) {
                    items.value = (response.data ?: emptyList()).sortedByDescending { it.qty }
                } else {
                    items.value = emptyList()
                    errorMessage.value = "Data belum tersedia"
                }
            } catch (e: Exception) {
                // API belum tersedia di backend — tampilkan pesan
                items.value = emptyList()
                errorMessage.value = "Fitur ini membutuhkan API backend yang belum tersedia"
            } finally {
                isLoading.value = false
            }
        }
    }
}
