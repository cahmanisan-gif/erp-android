package com.rajavavapor.app.ui.member

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.MemberItem
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MemberViewModel : ViewModel() {

    val results = MutableLiveData<List<MemberItem>>(emptyList())
    val isSearching = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    private var searchJob: Job? = null

    fun search(context: Context, query: String) {
        if (query.length < 3) {
            results.value = emptyList()
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // debounce
            isSearching.value = true
            try {
                val token = SessionManager(context).bearerToken()
                val response = ApiClient.service.searchMember(token, query)
                if (response.success) results.value = response.data ?: emptyList()
                else errorMessage.value = "Pencarian gagal"
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isSearching.value = false
            }
        }
    }
}
