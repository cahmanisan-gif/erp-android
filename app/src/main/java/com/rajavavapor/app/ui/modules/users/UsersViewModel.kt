package com.rajavavapor.app.ui.modules.users

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.data.UserListItem
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {

    val items = MutableLiveData<List<UserListItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    /** Holds all items for local filtering. */
    var allItems: List<UserListItem> = emptyList()
        private set

    fun load(context: Context) {
        val token = SessionManager(context).bearerToken()
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = ApiClient.service.getUsers(token)
                if (response.success) {
                    allItems = response.data ?: emptyList()
                    items.value = allItems
                } else {
                    errorMessage.value = response.message ?: "Gagal memuat data user"
                }
            } catch (e: Exception) {
                errorMessage.value = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
