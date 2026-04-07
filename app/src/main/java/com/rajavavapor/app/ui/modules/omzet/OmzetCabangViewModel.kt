package com.rajavavapor.app.ui.modules.omzet

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.OmzetCabangItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class OmzetCabangViewModel : ViewModel() {

    val items = MutableLiveData<List<OmzetCabangItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context, periode: String = "bulan-ini") {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Try dashboard/owner first (fast, already cached by server)
                val dashboard = withTimeoutOrNull(8000) {
                    ApiClient.service.getDashboardOwner(token)
                }
                if (dashboard?.success == true && !dashboard.data?.topCabang.isNullOrEmpty()) {
                    val converted = dashboard.data!!.topCabang!!.map { tc ->
                        OmzetCabangItem(
                            cabangId = null, kode = tc.kode, nama = tc.nama,
                            namaCabang = tc.nama, posTotal = tc.omzet,
                            posCash = null, posTransfer = null,
                            omzet = tc.omzet, trx = tc.trx, totalTrx = tc.trx
                        )
                    }.sortedByDescending { it.getDisplayOmzet() }
                    items.value = converted
                } else {
                    items.value = emptyList()
                    errorMessage.value = "Data belum tersedia"
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
