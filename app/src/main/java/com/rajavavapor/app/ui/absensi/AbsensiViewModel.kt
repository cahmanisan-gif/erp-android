package com.rajavavapor.app.ui.absensi

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.AbsensiClockData
import com.rajavavapor.app.data.AbsensiRiwayatItem
import com.rajavavapor.app.data.AbsensiStatusData
import com.rajavavapor.app.data.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AbsensiViewModel : ViewModel() {

    val status = MutableLiveData<AbsensiStatusData?>()
    val riwayat = MutableLiveData<List<AbsensiRiwayatItem>>(emptyList())
    val summary = MutableLiveData<Int?>(null)
    val isLoading = MutableLiveData(false)
    val isSubmitting = MutableLiveData(false)
    val clockResult = MutableLiveData<AbsensiClockData?>()
    val errorMessage = MutableLiveData<String?>()

    fun loadStatus(context: Context) {
        val token = SessionManager(context).bearerToken() ?: return
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = ApiClient.service.getAbsensiStatus(token)
                if (response.success) {
                    status.value = response.data
                }
            } catch (e: Exception) {
                errorMessage.value = "Gagal memuat status absensi"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loadRiwayat(context: Context, bulan: Int? = null, tahun: Int? = null) {
        val token = SessionManager(context).bearerToken() ?: return
        viewModelScope.launch {
            try {
                val response = ApiClient.service.getAbsensiRiwayat(token, bulan, tahun)
                if (response.success) {
                    riwayat.value = response.data ?: emptyList()
                    summary.value = response.summary?.hadir
                }
            } catch (e: Exception) {
                errorMessage.value = "Gagal memuat riwayat absensi"
            }
        }
    }

    fun clock(
        context: Context,
        tipe: String,
        photoFile: File,
        latitude: Double,
        longitude: Double,
        accuracy: Float,
        barcode: String? = null
    ) {
        val session = SessionManager(context)
        val token = session.bearerToken() ?: return
        val user = session.getUser()
        val cabangId = user?.cabangId?.toString() ?: "0"

        viewModelScope.launch {
            isSubmitting.value = true
            try {
                val textType = "text/plain".toMediaType()
                val imageType = "image/jpeg".toMediaType()

                val fotoPart = MultipartBody.Part.createFormData(
                    "foto", photoFile.name, photoFile.asRequestBody(imageType)
                )
                val latBody = latitude.toString().toRequestBody(textType)
                val lngBody = longitude.toString().toRequestBody(textType)
                val accBody = accuracy.toString().toRequestBody(textType)
                val tipeBody = tipe.toRequestBody(textType)
                val cabangBody = cabangId.toRequestBody(textType)
                val barcodeBody = barcode?.toRequestBody(textType)

                val response = ApiClient.service.clockAbsensi(
                    token, fotoPart, latBody, lngBody, accBody, tipeBody, cabangBody, barcodeBody
                )

                if (response.success) {
                    clockResult.value = response.data
                    loadStatus(context)
                    loadRiwayat(context)
                } else {
                    errorMessage.value = response.message ?: "Gagal melakukan absensi"
                }
            } catch (e: Exception) {
                errorMessage.value = "Gagal mengirim data absensi. Periksa koneksi internet."
            } finally {
                isSubmitting.value = false
            }
        }
    }

    fun clearClockResult() {
        clockResult.value = null
    }
}
