package com.rajavavapor.app.ui.absensi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.rajavavapor.app.databinding.FragmentAbsensiBinding
import com.rajavavapor.app.ui.barcode.BarcodeScannerActivity
import com.rajavavapor.app.util.LocationHelper
import com.rajavavapor.app.util.LocationResult
import com.rajavavapor.app.util.PhotoHelper
import kotlinx.coroutines.launch
import java.io.File

class AbsensiFragment : Fragment() {

    private var _binding: FragmentAbsensiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AbsensiViewModel by viewModels()
    private lateinit var adapter: AbsensiRiwayatAdapter
    private lateinit var locationHelper: LocationHelper

    private var currentPhotoFile: File? = null
    private var currentLocation: LocationResult? = null
    private var currentBarcode: String? = null

    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoFile?.exists() == true) {
            binding.imgSelfiePreview.isVisible = true
            Glide.with(this)
                .load(currentPhotoFile)
                .centerCrop()
                .into(binding.imgSelfiePreview)
        } else {
            currentPhotoFile = null
        }
    }

    // Barcode scanner launcher
    private val barcodeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            currentBarcode = result.data?.getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_RESULT)
            if (!currentBarcode.isNullOrEmpty()) {
                binding.tvBarcodeInfo.isVisible = true
                binding.tvBarcodeInfo.text = "Barcode: $currentBarcode"
            }
        }
    }

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] == true
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

        if (cameraGranted) {
            launchCamera()
        } else {
            showSnackbar("Izin kamera diperlukan untuk absensi")
        }

        if (locationGranted) {
            fetchLocation()
        } else {
            showSnackbar("Izin lokasi diperlukan untuk absensi")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAbsensiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationHelper = LocationHelper(requireActivity())
        setupRecyclerView()
        setupSwipeRefresh()
        setupButtons()
        observeViewModel()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = AbsensiRiwayatAdapter()
        binding.recyclerViewRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRiwayat.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), com.rajavavapor.app.R.color.brand_red)
        )
        binding.swipeRefresh.setOnRefreshListener {
            resetState()
            loadData()
        }
    }

    private fun setupButtons() {
        binding.btnSelfie.setOnClickListener {
            checkPermissionsAndLaunchCamera()
        }

        binding.btnBarcode.setOnClickListener {
            val intent = Intent(requireContext(), BarcodeScannerActivity::class.java)
            barcodeLauncher.launch(intent)
        }

        binding.btnClockIn.setOnClickListener {
            submitClock("masuk")
        }

        binding.btnClockOut.setOnClickListener {
            submitClock("pulang")
        }
    }

    private fun observeViewModel() {
        viewModel.status.observe(viewLifecycleOwner) { data ->
            if (data == null) return@observe

            val status = data.status?.lowercase() ?: ""
            when {
                !data.clockOut.isNullOrEmpty() -> {
                    binding.tvStatusText.text = "Sudah clock out pukul ${data.clockOut}"
                    binding.btnClockIn.isVisible = false
                    binding.btnClockOut.isVisible = false
                }
                !data.clockIn.isNullOrEmpty() -> {
                    binding.tvStatusText.text = "Sudah clock in pukul ${data.clockIn}"
                    binding.btnClockIn.isVisible = false
                    binding.btnClockOut.isVisible = true
                }
                data.izin != null -> {
                    binding.tvStatusText.text = "Izin: ${data.izin}"
                    binding.btnClockIn.isVisible = false
                    binding.btnClockOut.isVisible = false
                }
                else -> {
                    binding.tvStatusText.text = "Belum clock in hari ini"
                    binding.btnClockIn.isVisible = true
                    binding.btnClockOut.isVisible = false
                }
            }
        }

        viewModel.riwayat.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.tvEmptyRiwayat.isVisible = items.isEmpty()
            binding.recyclerViewRiwayat.isVisible = items.isNotEmpty()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefresh.isRefreshing = loading
        }

        viewModel.isSubmitting.observe(viewLifecycleOwner) { submitting ->
            binding.progressSubmit.isVisible = submitting
            binding.btnClockIn.isEnabled = !submitting
            binding.btnClockOut.isEnabled = !submitting
            binding.btnSelfie.isEnabled = !submitting
            binding.btnBarcode.isEnabled = !submitting
        }

        viewModel.clockResult.observe(viewLifecycleOwner) { data ->
            if (data == null) return@observe
            showSnackbar("Absensi berhasil!")
            resetState()
            viewModel.clearClockResult()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                showSnackbar(msg)
                viewModel.errorMessage.value = null
            }
        }
    }

    private fun loadData() {
        viewModel.loadStatus(requireContext())
        viewModel.loadRiwayat(requireContext())
    }

    private fun checkPermissionsAndLaunchCamera() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            launchCamera()
            fetchLocation()
        }
    }

    private fun launchCamera() {
        val file = PhotoHelper.createTempPhotoFile(requireContext())
        currentPhotoFile = file
        val uri = PhotoHelper.getPhotoUri(requireContext(), file)
        cameraLauncher.launch(uri)
    }

    private fun fetchLocation() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.tvLocationInfo.text = "Mengambil lokasi GPS..."
            val loc = locationHelper.getCurrentLocation()
            if (loc != null) {
                currentLocation = loc
                binding.tvLocationInfo.text = "Lokasi: ${loc.latitude}, ${loc.longitude} (akurasi: ${loc.accuracy.toInt()}m)"
            } else {
                binding.tvLocationInfo.text = "Tidak dapat mengambil lokasi"
                showSnackbar("Gagal mengambil lokasi GPS. Coba lagi.")
            }
        }
    }

    private fun submitClock(tipe: String) {
        val photo = currentPhotoFile
        val loc = currentLocation

        if (photo == null || !photo.exists()) {
            showSnackbar("Ambil foto selfie terlebih dahulu")
            return
        }

        if (loc == null) {
            showSnackbar("Menunggu lokasi GPS...")
            fetchLocation()
            return
        }

        viewModel.clock(requireContext(), tipe, photo, loc.latitude, loc.longitude, loc.accuracy, currentBarcode)
    }

    private fun resetState() {
        PhotoHelper.deletePhotoFile(currentPhotoFile)
        currentPhotoFile = null
        currentLocation = null
        currentBarcode = null
        binding.imgSelfiePreview.isVisible = false
        binding.tvLocationInfo.text = "Lokasi belum diambil"
        binding.tvBarcodeInfo.isVisible = false
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
