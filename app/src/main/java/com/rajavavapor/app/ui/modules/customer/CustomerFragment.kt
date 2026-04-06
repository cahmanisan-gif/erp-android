package com.rajavavapor.app.ui.modules.customer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rajavavapor.app.databinding.FragmentCustomerBinding
import com.rajavavapor.app.util.ScreenHelper

class CustomerFragment : Fragment() {

    private var _binding: FragmentCustomerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomerViewModel by viewModels()
    private lateinit var adapter: CustomerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CustomerAdapter { _ -> }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), ScreenHelper.getGridColumns(requireContext()))
        binding.recyclerView.adapter = adapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val q = s?.toString()?.trim() ?: ""
                viewModel.search(requireContext(), q)
                binding.tvHint.visibility = if (q.length < 3) View.VISIBLE else View.GONE
            }
        })

        viewModel.isSearching.observe(viewLifecycleOwner) { searching ->
            binding.progressBar.visibility = if (searching) View.VISIBLE else View.GONE
        }

        viewModel.results.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            val query = binding.etSearch.text?.toString()?.trim() ?: ""
            binding.tvEmpty.visibility =
                if (items.isEmpty() && query.length >= 3) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
