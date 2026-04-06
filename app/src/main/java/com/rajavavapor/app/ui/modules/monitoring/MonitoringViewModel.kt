package com.rajavavapor.app.ui.modules.monitoring

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.MonitoringOmzetItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class MonitoringViewModel : ViewModel() {

    private var allItems = listOf<MonitoringOmzetItem>()
    val items = MutableLiveData<List<MonitoringOmzetItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun filter(query: String) {
        if (query.length < 2) {
            items.value = allItems
            return
        }
        val q = query.lowercase()
        items.value = allItems.filter {
            (it.cabang?.lowercase()?.contains(q) == true)
        }
    }

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = ApiClient.service.getMonitoringOmzet(token)
                if (response.success) {
                    allItems = response.data ?: emptyList()
                    items.value = allItems
                }
                else errorMessage.value = response.message ?: "Gagal memuat data monitoring"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
