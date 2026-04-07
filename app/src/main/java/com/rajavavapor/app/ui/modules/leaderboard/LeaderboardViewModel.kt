package com.rajavavapor.app.ui.modules.leaderboard

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.LeaderboardItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class LeaderboardViewModel : ViewModel() {

    val items = MutableLiveData<List<LeaderboardItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context, type: String, periode: String = "bulan-ini") {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Use dashboard/owner (fast, reliable)
                val dashboard = withTimeoutOrNull(8000) {
                    ApiClient.service.getDashboardOwner(token)
                }
                if (dashboard?.success == true && dashboard.data != null) {
                    val list = if (type == "kasir") {
                        dashboard.data.topKasir?.map { k ->
                            LeaderboardItem(
                                id = null, nama = k.namaLengkap, namaLengkap = k.namaLengkap,
                                namaCabang = k.namaCabang, kodeCabang = null, kode = null,
                                subtitle = k.namaCabang, omzet = k.omzet, trx = k.trx
                            )
                        }
                    } else {
                        dashboard.data.topCabang?.map { c ->
                            LeaderboardItem(
                                id = null, nama = c.nama, namaLengkap = null,
                                namaCabang = c.nama, kodeCabang = c.kode, kode = c.kode,
                                subtitle = c.kode, omzet = c.omzet, trx = c.trx
                            )
                        }
                    }
                    items.value = (list ?: emptyList()).sortedByDescending { it.omzet }
                } else {
                    items.value = emptyList()
                    errorMessage.value = "Data belum tersedia"
                }
            } catch (e: Exception) {
                items.value = emptyList()
                errorMessage.value = "Gagal memuat leaderboard"
            } finally {
                isLoading.value = false
            }
        }
    }
}
