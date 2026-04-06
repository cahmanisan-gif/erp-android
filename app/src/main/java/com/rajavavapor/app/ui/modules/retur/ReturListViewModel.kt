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

    val items = MutableLiveData<List<ReturItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = ApiClient.service.getRetur(token)
                if (response.success) items.value = response.data ?: emptyList()
                else errorMessage.value = "Gagal memuat data retur"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
