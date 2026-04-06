package com.rajavavapor.app.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.databinding.FragmentMenuBinding
import com.rajavavapor.app.util.ScreenHelper

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        val user = session.getUser()
        val role = user?.role ?: "kasir"

        binding.tvMenuGreeting.text = "Menu"
        binding.tvMenuRole.text = formatRole(role)

        val menuItems = MenuConfig.getMenuForRole(role)
        binding.tvMenuCount.text = "${menuItems.size} fitur"

        val columns = if (ScreenHelper.isTablet(requireContext())) 5 else 3
        binding.recyclerMenu.layoutManager = GridLayoutManager(requireContext(), columns)
        binding.recyclerMenu.adapter = MenuAdapter(menuItems) { item ->
            item.navDestination?.let { dest ->
                try {
                    findNavController().navigate(dest)
                } catch (_: Exception) {
                    // Destination not yet registered
                }
            }
        }
    }

    private fun formatRole(role: String): String = when (role) {
        "owner" -> "Owner — Akses Penuh"
        "manajer" -> "Manajer"
        "head_operational" -> "Head Operational"
        "admin_pusat" -> "Admin Pusat"
        "spv_area" -> "Supervisor Area"
        "finance" -> "Finance"
        "kepala_cabang" -> "Kepala Cabang"
        "sales" -> "Sales"
        "kasir_sales" -> "Kasir Sales"
        "kasir" -> "Kasir"
        "vaporista" -> "Vaporista"
        else -> role.replaceFirstChar { it.uppercase() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
