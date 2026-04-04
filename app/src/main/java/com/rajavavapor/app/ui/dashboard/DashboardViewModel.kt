package com.rajavavapor.app.ui.dashboard

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.OwnerDashboardData
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.data.StatsData
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    val ownerData = MutableLiveData<OwnerDashboardData?>()
    val statsData = MutableLiveData<StatsData?>()
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    private val ownerRoles = setOf(
        "owner", "manajer", "manajer_area", "head_operational", "admin_pusat"
    )

    fun load(context: Context) {
        val session = SessionManager(context)
        val token = session.bearerToken()
        val role = session.getUser()?.role ?: ""

        viewModelScope.launch {
            isLoading.value = true
            try {
                if (role in ownerRoles) {
                    val response = ApiClient.service.getDashboardOwner(token)
                    if (response.success) ownerData.value = response.data
                    else errorMessage.value = response.message
                } else {
                    val response = ApiClient.service.getDashboardStats(token)
                    if (response.success) statsData.value = response.data
                    else errorMessage.value = response.message
                }
            } catch (e: Exception) {
                errorMessage.value = "Gagal memuat data: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
