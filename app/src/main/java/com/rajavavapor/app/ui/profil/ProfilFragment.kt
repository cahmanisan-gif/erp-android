package com.rajavavapor.app.ui.profil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.databinding.FragmentProfilBinding
import com.rajavavapor.app.ui.main.MainActivity

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = SessionManager(requireContext()).getUser()
        if (user != null) {
            binding.tvNamaLengkap.text = user.namaLengkap ?: user.username
            binding.tvUsername.text = "@${user.username}"
            binding.tvRole.text = formatRole(user.role)
            binding.tvCabang.text = user.namaCabang ?: "Pusat"
            binding.tvInitial.text = (user.namaLengkap ?: user.username).take(1).uppercase()
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Keluar") { _, _ ->
                    SessionManager(requireContext()).logout()
                    (activity as? MainActivity)?.logout()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        binding.tvAppVersion.text = "Raja Vapor Portal v2.1.0 • poinraja.com"
    }

    private fun formatRole(role: String): String = when (role) {
        "owner" -> "Owner"
        "manajer" -> "Manajer"
        "manajer_area" -> "Manajer Area"
        "head_operational" -> "Head Operational"
        "admin_pusat" -> "Admin Pusat"
        "kasir" -> "Kasir"
        "kasir_sales" -> "Kasir Sales"
        "kepala_cabang" -> "Kepala Cabang"
        "vaporista" -> "Vaporista"
        else -> role.replace("_", " ").replaceFirstChar { it.uppercase() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
