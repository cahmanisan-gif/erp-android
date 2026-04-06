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

    private var allItems = listOf<InvoiceItem>()
    val results = MutableLiveData<List<InvoiceItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun filter(query: String) {
        if (query.length < 2) {
            results.value = allItems
            return
        }
        val q = query.lowercase()
        results.value = allItems.filter {
            (it.noInvoice?.lowercase()?.contains(q) == true) ||
            (it.namaCustomer?.lowercase()?.contains(q) == true)
        }
    }

    fun loadInvoices(context: Context) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val token = SessionManager(context).bearerToken()
                val response = ApiClient.service.getInvoices(token, 1)
                if (response.success) {
                    allItems = response.data ?: emptyList()
                    results.value = allItems
                }
                else errorMessage.value = "Gagal memuat invoice"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
