package com.rajavavapor.app.ui.modules.omzet

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.OmzetCabangItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class OmzetCabangViewModel : ViewModel() {

    val items = MutableLiveData<List<OmzetCabangItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = ApiClient.service.getOmzetCabang(token)
                if (response.success) {
                    // Sort by omzet descending
                    val sorted = (response.data ?: emptyList()).sortedByDescending { it.omzet }
                    items.value = sorted
                }
            } catch (e: Exception) {
                errorMessage.value = "Gagal memuat data omzet cabang"
            } finally {
                isLoading.value = false
            }
        }
    }
}
