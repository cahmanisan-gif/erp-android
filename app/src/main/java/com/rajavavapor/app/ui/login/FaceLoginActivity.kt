package com.rajavavapor.app.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.FaceEmployee
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.databinding.ActivityFaceLoginBinding
import com.rajavavapor.app.ui.main.MainActivity
import com.rajavavapor.app.util.FaceDetectionHelper
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceLoginBinding
    private lateinit var session: SessionManager
    private lateinit var adapter: EmployeeAdapter
    private lateinit var cameraExecutor: ExecutorService

    private var selectedEmployee: FaceEmployee? = null
    private var imageCapture: ImageCapture? = null
    private var faceReady = false
    private var isVerifying = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else {
            showSnackbar("Izin kamera diperlukan")
            showEmployeeList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setupEmployeeList()
        setupButtons()
        loadEmployees()
    }

    private fun setupEmployeeList() {
        adapter = EmployeeAdapter { employee ->
            selectedEmployee = employee
            showFaceCamera()
        }
        binding.recyclerEmployees.layoutManager = LinearLayoutManager(this)
        binding.recyclerEmployees.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnBackToLogin.setOnClickListener { finish() }

        binding.btnBackToList.setOnClickListener {
            stopCamera()
            showEmployeeList()
        }

        binding.btnCaptureFace.setOnClickListener {
            if (!isVerifying && faceReady) {
                captureAndVerify()
            }
        }
    }

    private fun loadEmployees() {
        // Get cabang_id from last saved user or from intent
        val cabangId = session.getUser()?.cabangId
            ?: intent.getIntExtra("cabang_id", 0)

        if (cabangId == 0) {
            binding.tvErrorEmployees.isVisible = true
            binding.tvErrorEmployees.text = "Login dengan username terlebih dahulu untuk mendeteksi cabang"
            return
        }

        binding.tvBranchName.text = session.getUser()?.namaCabang ?: ""
        binding.progressEmployees.isVisible = true

        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getFaceEmployees(cabangId)
                binding.progressEmployees.isVisible = false
                if (response.success && !response.data.isNullOrEmpty()) {
                    adapter.submitList(response.data)
                } else {
                    binding.tvErrorEmployees.isVisible = true
                    binding.tvErrorEmployees.text = "Tidak ada karyawan terdaftar di cabang ini"
                }
            } catch (e: Exception) {
                binding.progressEmployees.isVisible = false
                binding.tvErrorEmployees.isVisible = true
                binding.tvErrorEmployees.text = "Gagal memuat data karyawan. Periksa koneksi."
            }
        }
    }

    private fun showFaceCamera() {
        binding.layoutSelectEmployee.isVisible = false
        binding.layoutFaceCamera.isVisible = true
        binding.tvSelectedName.text = selectedEmployee?.namaLengkap ?: ""
        binding.btnCaptureFace.isEnabled = false
        faceReady = false

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun showEmployeeList() {
        binding.layoutFaceCamera.isVisible = false
        binding.layoutSelectEmployee.isVisible = true
        selectedEmployee = null
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.previewViewFace.surfaceProvider
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                analyzeFace(imageProxy)
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageCapture,
                imageAnalysis
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        try {
            ProcessCameraProvider.getInstance(this).get().unbindAll()
        } catch (_: Exception) {}
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun analyzeFace(imageProxy: ImageProxy) {
        if (isVerifying) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val inputImage = com.google.mlkit.vision.common.InputImage.fromMediaImage(
            mediaImage, imageProxy.imageInfo.rotationDegrees
        )

        val options = com.google.mlkit.vision.face.FaceDetectorOptions.Builder()
            .setPerformanceMode(com.google.mlkit.vision.face.FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(com.google.mlkit.vision.face.FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.3f)
            .build()

        com.google.mlkit.vision.face.FaceDetection.getClient(options)
            .process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty() && !isVerifying) {
                    val face = faces[0]
                    val yAngle = face.headEulerAngleY
                    val zAngle = face.headEulerAngleZ
                    val leftEye = face.leftEyeOpenProbability ?: 1f
                    val rightEye = face.rightEyeOpenProbability ?: 1f
                    val bounds = face.boundingBox

                    val isFrontal = Math.abs(yAngle) < 25 && Math.abs(zAngle) < 15
                    val eyesOpen = leftEye > 0.4f && rightEye > 0.4f
                    val bigEnough = bounds.width() > 80 && bounds.height() > 80

                    runOnUiThread {
                        when {
                            !bigEnough -> {
                                binding.tvFaceStatus.text = "Dekatkan wajah ke kamera"
                                setFaceReady(false)
                            }
                            !isFrontal -> {
                                binding.tvFaceStatus.text = "Hadapkan wajah ke depan"
                                setFaceReady(false)
                            }
                            !eyesOpen -> {
                                binding.tvFaceStatus.text = "Buka mata Anda"
                                setFaceReady(false)
                            }
                            else -> {
                                binding.tvFaceStatus.text = "Wajah terdeteksi — Tekan tombol verifikasi"
                                setFaceReady(true)
                            }
                        }
                    }
                } else if (!isVerifying) {
                    runOnUiThread {
                        binding.tvFaceStatus.text = "Posisikan wajah di dalam bingkai"
                        setFaceReady(false)
                    }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    private fun setFaceReady(ready: Boolean) {
        faceReady = ready
        binding.btnCaptureFace.isEnabled = ready && !isVerifying
    }

    private fun captureAndVerify() {
        val capture = imageCapture ?: return
        isVerifying = true
        binding.progressVerify.isVisible = true
        binding.btnCaptureFace.isEnabled = false
        binding.tvFaceStatus.text = "Memverifikasi wajah..."

        val photoFile = File(cacheDir, "face_login_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    sendToServer(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    isVerifying = false
                    binding.progressVerify.isVisible = false
                    binding.btnCaptureFace.isEnabled = true
                    showSnackbar("Gagal mengambil foto. Coba lagi.")
                }
            }
        )
    }

    private fun sendToServer(photoFile: File) {
        val employee = selectedEmployee ?: return

        lifecycleScope.launch {
            try {
                val imageType = "image/jpeg".toMediaType()
                val textType = "text/plain".toMediaType()

                val fotoPart = MultipartBody.Part.createFormData(
                    "foto", photoFile.name, photoFile.asRequestBody(imageType)
                )
                val idBody = employee.id.toString().toRequestBody(textType)

                val response = ApiClient.service.verifyFace(fotoPart, idBody)

                if (response.success && response.token != null && response.user != null) {
                    // Login successful
                    session.saveToken(response.token)
                    session.saveUser(response.user)
                    session.saveCabangId(response.user.cabangId ?: 0)

                    startActivity(Intent(this@FaceLoginActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                } else {
                    isVerifying = false
                    binding.progressVerify.isVisible = false
                    binding.btnCaptureFace.isEnabled = faceReady
                    binding.tvFaceStatus.text = "Posisikan wajah di dalam bingkai"
                    showSnackbar(response.message ?: "Wajah tidak cocok. Coba lagi.")
                }
            } catch (e: Exception) {
                isVerifying = false
                binding.progressVerify.isVisible = false
                binding.btnCaptureFace.isEnabled = faceReady
                showSnackbar("Gagal verifikasi. Periksa koneksi internet.")
            } finally {
                photoFile.delete()
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
