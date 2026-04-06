package com.rajavavapor.app.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.rajavavapor.app.api.ApiClient
import com.rajavavapor.app.data.FaceCabang
import com.rajavavapor.app.data.FaceEmployee
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.databinding.ActivityFaceLoginBinding
import com.rajavavapor.app.ui.main.MainActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceLoginBinding
    private lateinit var session: SessionManager
    private lateinit var cabangAdapter: CabangAdapter
    private lateinit var employeeAdapter: EmployeeAdapter
    private lateinit var cameraExecutor: ExecutorService

    private var selectedCabang: FaceCabang? = null
    private var selectedEmployee: FaceEmployee? = null
    private var imageCapture: ImageCapture? = null
    private var faceReady = false
    private var lastFaceConfidence = 0f
    private var isVerifying = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else {
            showSnackbar("Izin kamera diperlukan")
            showStep2()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityFaceLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        session = SessionManager(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setupAdapters()
        setupButtons()
        loadCabang()
    }

    // ── Adapters ─────────────────────────────────────────────────────────────

    private fun setupAdapters() {
        cabangAdapter = CabangAdapter { cabang ->
            selectedCabang = cabang
            session.saveCabangId(cabang.id)
            showStep2()
            loadEmployees(cabang.id)
        }
        binding.recyclerCabang.layoutManager = LinearLayoutManager(this)
        binding.recyclerCabang.adapter = cabangAdapter

        employeeAdapter = EmployeeAdapter { employee ->
            selectedEmployee = employee
            showStep3()
        }
        binding.recyclerEmployees.layoutManager = LinearLayoutManager(this)
        binding.recyclerEmployees.adapter = employeeAdapter
    }

    // ── Buttons ──────────────────────────────────────────────────────────────

    private fun setupButtons() {
        binding.btnBackToLogin.setOnClickListener { finish() }

        binding.btnBackToCabang.setOnClickListener {
            showStep1()
        }

        binding.btnBackToList.setOnClickListener {
            stopCamera()
            showStep2()
        }

        binding.btnCaptureFace.setOnClickListener {
            if (!isVerifying && faceReady) captureAndVerify()
        }
    }

    // ── Step Navigation ──────────────────────────────────────────────────────

    private fun showStep1() {
        binding.layoutSelectCabang.isVisible = true
        binding.layoutSelectEmployee.isVisible = false
        binding.layoutFaceCamera.isVisible = false
    }

    private fun showStep2() {
        binding.layoutSelectCabang.isVisible = false
        binding.layoutSelectEmployee.isVisible = true
        binding.layoutFaceCamera.isVisible = false
        binding.tvBranchName.text = selectedCabang?.nama ?: ""
    }

    private fun showStep3() {
        binding.layoutSelectCabang.isVisible = false
        binding.layoutSelectEmployee.isVisible = false
        binding.layoutFaceCamera.isVisible = true
        binding.tvSelectedName.text = selectedEmployee?.namaLengkap ?: ""
        binding.btnCaptureFace.isEnabled = false
        faceReady = false
        lastFaceConfidence = 0f
        isVerifying = false

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // ── API Calls ────────────────────────────────────────────────────────────

    private fun loadCabang() {
        binding.progressCabang.isVisible = true
        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getFaceCabang()
                binding.progressCabang.isVisible = false
                if (response.success && !response.data.isNullOrEmpty()) {
                    cabangAdapter.submitList(response.data)
                } else {
                    binding.tvErrorCabang.isVisible = true
                    binding.tvErrorCabang.text = "Tidak ada cabang tersedia"
                }
            } catch (e: Exception) {
                binding.progressCabang.isVisible = false
                binding.tvErrorCabang.isVisible = true
                binding.tvErrorCabang.text = "Gagal memuat cabang. Periksa koneksi."
            }
        }
    }

    private fun loadEmployees(cabangId: Int) {
        binding.progressEmployees.isVisible = true
        binding.tvErrorEmployees.isVisible = false
        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getFaceEmployees(cabangId)
                binding.progressEmployees.isVisible = false
                if (response.success && !response.data.isNullOrEmpty()) {
                    employeeAdapter.submitList(response.data)
                } else {
                    binding.tvErrorEmployees.isVisible = true
                    binding.tvErrorEmployees.text = "Tidak ada karyawan terdaftar"
                }
            } catch (e: Exception) {
                binding.progressEmployees.isVisible = false
                binding.tvErrorEmployees.isVisible = true
                binding.tvErrorEmployees.text = "Gagal memuat data karyawan"
            }
        }
    }

    // ── Camera ───────────────────────────────────────────────────────────────

    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.previewViewFace.surfaceProvider)

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(cameraExecutor, ::analyzeFace) }

            provider.unbindAll()
            provider.bindToLifecycle(
                this, CameraSelector.DEFAULT_FRONT_CAMERA,
                preview, imageCapture, analysis
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        try { ProcessCameraProvider.getInstance(this).get().unbindAll() } catch (_: Exception) {}
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun analyzeFace(imageProxy: ImageProxy) {
        if (isVerifying) { imageProxy.close(); return }

        val media = imageProxy.image
        if (media == null) { imageProxy.close(); return }

        val input = InputImage.fromMediaImage(media, imageProxy.imageInfo.rotationDegrees)

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.3f)
            .build()

        FaceDetection.getClient(options).process(input)
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

                    // Calculate confidence from face quality metrics
                    val angleScore = 1f - (Math.abs(yAngle) + Math.abs(zAngle)) / 40f
                    val eyeScore = (leftEye + rightEye) / 2f
                    val sizeScore = (bounds.width().coerceAtMost(300) / 300f)
                    lastFaceConfidence = ((angleScore + eyeScore + sizeScore) / 3f).coerceIn(0f, 1f)

                    runOnUiThread {
                        when {
                            !bigEnough -> updateFaceStatus("Dekatkan wajah ke kamera", false)
                            !isFrontal -> updateFaceStatus("Hadapkan wajah ke depan", false)
                            !eyesOpen -> updateFaceStatus("Buka mata Anda", false)
                            else -> updateFaceStatus("Wajah terdeteksi — Tekan verifikasi", true)
                        }
                    }
                } else if (!isVerifying) {
                    runOnUiThread { updateFaceStatus("Posisikan wajah di dalam bingkai", false) }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    private fun updateFaceStatus(text: String, ready: Boolean) {
        binding.tvFaceStatus.text = text
        faceReady = ready
        binding.btnCaptureFace.isEnabled = ready && !isVerifying
    }

    // ── Capture & Verify ─────────────────────────────────────────────────────

    private fun captureAndVerify() {
        val capture = imageCapture ?: return
        isVerifying = true
        binding.progressVerify.isVisible = true
        binding.btnCaptureFace.isEnabled = false
        binding.tvFaceStatus.text = "Memverifikasi wajah..."

        val photoFile = File(cacheDir, "face_${System.currentTimeMillis()}.jpg")
        capture.takePicture(
            ImageCapture.OutputFileOptions.Builder(photoFile).build(),
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    sendToServer(photoFile)
                }

                override fun onError(e: ImageCaptureException) {
                    isVerifying = false
                    binding.progressVerify.isVisible = false
                    binding.btnCaptureFace.isEnabled = faceReady
                    showSnackbar("Gagal mengambil foto")
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
                val userIdBody = employee.id.toString().toRequestBody(textType)
                val confidenceBody = lastFaceConfidence.toString().toRequestBody(textType)

                val response = ApiClient.service.verifyFace(fotoPart, userIdBody, confidenceBody)

                if (response.success && response.token != null && response.user != null) {
                    session.saveToken(response.token)
                    session.saveUser(response.user)
                    session.saveCabangId(selectedCabang?.id ?: 0)

                    startActivity(Intent(this@FaceLoginActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                } else {
                    resetVerifyState()
                    showSnackbar(response.message ?: "Wajah tidak cocok. Coba lagi.")
                }
            } catch (e: Exception) {
                resetVerifyState()
                showSnackbar("Gagal verifikasi. Periksa koneksi internet.")
            } finally {
                photoFile.delete()
            }
        }
    }

    private fun resetVerifyState() {
        isVerifying = false
        binding.progressVerify.isVisible = false
        binding.btnCaptureFace.isEnabled = faceReady
        binding.tvFaceStatus.text = "Posisikan wajah di dalam bingkai"
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
