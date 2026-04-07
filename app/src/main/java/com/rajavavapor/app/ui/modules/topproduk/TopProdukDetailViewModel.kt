package com.rajavavapor.app.ui.modules.topproduk

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.BestSellerDetailResponse
import com.rajavavapor.app.data.ProdukCabangItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class TopProdukDetailViewModel : ViewModel() {

    val items = MutableLiveData<List<ProdukCabangItem>>(emptyList())
    val detail = MutableLiveData<BestSellerDetailResponse?>()
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context, produkId: Int) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = withTimeoutOrNull(8000) {
                    ApiClient.service.getBestSellerDetail(token, produkId, "bulan")
                }
                if (response?.success == true) {
                    detail.value = response
                    items.value = (response.perCabang ?: emptyList()).sortedByDescending { it.qty }
                } else {
                    items.value = emptyList()
                    errorMessage.value = "Data belum tersedia"
                }
            } catch (e: Exception) {
                items.value = emptyList()
                errorMessage.value = "Gagal memuat detail produk"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loadByName(context: Context, namaProduk: String) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                val list = withTimeoutOrNull(5000) {
                    ApiClient.service.getBestSeller(token, "bulan", null, null, 50)
                }
                val match = list?.data?.find {
                    it.namaProduk?.equals(namaProduk, ignoreCase = true) == true
                }
                if (match?.produkId != null) {
                    load(context, match.produkId)
                } else {
                    items.value = emptyList()
                    errorMessage.value = "Produk tidak ditemukan"
                }
            } catch (e: Exception) {
                items.value = emptyList()
                errorMessage.value = "Gagal memuat data"
            } finally {
                isLoading.value = false
            }
        }
    }
}
