package com.rajavavapor.app.ui.modules.pos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rajavavapor.app.databinding.FragmentPosBinding
import com.rajavavapor.app.ui.barcode.BarcodeScannerActivity
import com.rajavavapor.app.util.AnimationHelper
import com.rajavavapor.app.util.ScreenHelper
import java.text.NumberFormat
import java.util.Locale

class PosFragment : Fragment() {

    private var _binding: FragmentPosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PosViewModel by viewModels()
    private lateinit var adapter: PosAdapter

    // Barcode scanner launcher
    private val barcodeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val barcode = result.data?.getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_RESULT)
            if (!barcode.isNullOrEmpty()) {
                // Set barcode as search query
                binding.etSearch.setText(barcode)
                binding.etSearch.setSelection(barcode.length)
                viewModel.search(requireContext(), barcode)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val columns = if (ScreenHelper.isTablet(requireContext())) 3 else 2
        adapter = PosAdapter { _ -> }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
        binding.recyclerView.adapter = adapter

        // Text search
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val q = s?.toString()?.trim() ?: ""
                viewModel.search(requireContext(), q)
                binding.tvHint.visibility = if (q.length < 3) View.VISIBLE else View.GONE
            }
        })

        // Barcode scan button
        binding.btnScanBarcode.setOnClickListener {
            AnimationHelper.hapticClick(it)
            val intent = Intent(requireContext(), BarcodeScannerActivity::class.java)
            barcodeLauncher.launch(intent)
        }

        viewModel.isSearching.observe(viewLifecycleOwner) { searching ->
            binding.progressBar.visibility = if (searching) View.VISIBLE else View.GONE
        }

        viewModel.results.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            val query = binding.etSearch.text?.toString()?.trim() ?: ""
            binding.tvEmpty.visibility =
                if (items.isEmpty() && query.length >= 3) View.VISIBLE else View.GONE
            updateTotal(items)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun updateTotal(items: List<com.rajavavapor.app.data.ProdukItem>) {
        if (items.isEmpty()) {
            binding.cardTotal.visibility = View.GONE
            return
        }
        binding.cardTotal.visibility = View.VISIBLE
        val total = items.sumOf { it.hargaJual }
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        binding.tvTotal.text = "Rp ${formatter.format(total)}"
        binding.tvItemCount.text = "${items.size} produk"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
