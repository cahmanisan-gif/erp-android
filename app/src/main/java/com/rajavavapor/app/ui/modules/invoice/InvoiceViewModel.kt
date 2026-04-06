package com.rajavavapor.app.ui.modules.invoice

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.InvoiceItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class InvoiceViewModel : ViewModel() {

    val results = MutableLiveData<List<InvoiceItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun loadInvoices(context: Context) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val token = SessionManager(context).bearerToken()
                val response = ApiClient.service.getInvoices(token, 1)
                if (response.success) results.value = response.data ?: emptyList()
                else errorMessage.value = "Gagal memuat invoice"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
