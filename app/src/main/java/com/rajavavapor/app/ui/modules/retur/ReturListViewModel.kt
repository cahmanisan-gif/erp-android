package com.rajavavapor.app.ui.modules.retur

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.ReturItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class ReturListViewModel : ViewModel() {

    private var allItems = listOf<ReturItem>()
    val items = MutableLiveData<List<ReturItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun filter(query: String) {
        if (query.length < 2) {
            items.value = allItems
            return
        }
        val q = query.lowercase()
        items.value = allItems.filter {
            (it.noRetur?.lowercase()?.contains(q) == true) ||
            (it.namaCabang?.lowercase()?.contains(q) == true)
        }
    }

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = ApiClient.service.getRetur(token)
                if (response.success) {
                    allItems = response.data ?: emptyList()
                    items.value = allItems
                }
                else errorMessage.value = "Gagal memuat data retur"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
