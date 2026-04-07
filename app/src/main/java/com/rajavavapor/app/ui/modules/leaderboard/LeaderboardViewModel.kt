package com.rajavavapor.app.ui.modules.leaderboard

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.LeaderboardItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {

    val items = MutableLiveData<List<LeaderboardItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun load(context: Context, type: String, periode: String = "bulan-ini") {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = ApiClient.service.getLeaderboard(token, type, periode)
                if (response.success) {
                    items.value = (response.data ?: emptyList())
                        .sortedByDescending { it.omzet }
                }
            } catch (e: Exception) {
                // Fallback: use dashboard/owner top_kasir or top_cabang
                try {
                    val dashboard = ApiClient.service.getDashboardOwner(token)
                    if (dashboard.success && dashboard.data != null) {
                        val list = if (type == "kasir") {
                            dashboard.data.topKasir?.map { k ->
                                LeaderboardItem(
                                    id = null,
                                    nama = k.namaLengkap,
                                    namaCabang = k.namaCabang,
                                    kodeCabang = null,
                                    subtitle = k.namaCabang,
                                    omzet = k.omzet,
                                    trx = k.trx
                                )
                            }
                        } else {
                            dashboard.data.topCabang?.map { c ->
                                LeaderboardItem(
                                    id = null,
                                    nama = c.nama,
                                    namaCabang = c.nama,
                                    kodeCabang = c.kode,
                                    subtitle = c.kode,
                                    omzet = c.omzet,
                                    trx = c.trx
                                )
                            }
                        }
                        items.value = list ?: emptyList()
                    }
                } catch (_: Exception) {
                    errorMessage.value = "Gagal memuat leaderboard"
                }
            } finally {
                isLoading.value = false
            }
        }
    }
}
