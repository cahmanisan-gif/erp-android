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

    fun load(context: Context, periode: String = "bulan-ini") {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Try monitoring/omzet first (detailed per cabang)
                val response = ApiClient.service.getMonitoringOmzetDetail(token, periode)
                if (response.success) {
                    val sorted = (response.data ?: emptyList())
                        .sortedByDescending { it.getDisplayOmzet() }
                    items.value = sorted
                }
            } catch (e: Exception) {
                // Fallback: use top_cabang from dashboard/owner
                try {
                    val dashboard = ApiClient.service.getDashboardOwner(token)
                    if (dashboard.success && dashboard.data != null) {
                        val topCabang = dashboard.data.topCabang ?: emptyList()
                        val converted = topCabang.map { tc ->
                            OmzetCabangItem(
                                cabangId = null,
                                kode = tc.kode,
                                nama = tc.nama,
                                namaCabang = tc.nama,
                                posTotal = tc.omzet,
                                posCash = null,
                                posTransfer = null,
                                omzet = tc.omzet,
                                trx = tc.trx,
                                totalTrx = tc.trx
                            )
                        }
                        items.value = converted
                    }
                } catch (_: Exception) {
                    errorMessage.value = "Gagal memuat data omzet cabang"
                }
            } finally {
                isLoading.value = false
            }
        }
    }
}
