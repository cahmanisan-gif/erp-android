package com.rajavavapor.app.ui.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rajavavapor.app.R

/**
 * Reusable stub fragment for modules not yet fully implemented.
 * Shows the module name with a "Coming Soon" message.
 * Each nav destination uses a separate subclass so Navigation can distinguish them.
 */
open class StubFragment : Fragment() {

    open val moduleName: String = "Module"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(resources.getColor(R.color.gray_bg, null))
        }

        val title = TextView(requireContext()).apply {
            text = moduleName
            textSize = 22f
            setTextColor(resources.getColor(R.color.dark_bg, null))
            gravity = android.view.Gravity.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val subtitle = TextView(requireContext()).apply {
            text = "Sedang dalam pengembangan"
            textSize = 14f
            setTextColor(0xFF888888.toInt())
            gravity = android.view.Gravity.CENTER
            setPadding(0, 16, 0, 0)
        }

        layout.addView(title)
        layout.addView(subtitle)
        return layout
    }
}

// ── Concrete stub fragments for each module ──────────────────────────────────

// PosFragment -> moved to com.rajavavapor.app.ui.modules.pos.PosFragment
// InvoiceFragment -> moved to com.rajavavapor.app.ui.modules.invoice.InvoiceFragment
// CustomerFragment -> moved to com.rajavavapor.app.ui.modules.customer.CustomerFragment
// ProdukFragment -> moved to com.rajavavapor.app.ui.modules.produk.ProdukListFragment
// ReturFragment -> moved to com.rajavavapor.app.ui.modules.retur.ReturListFragment
// RequestFragment -> moved to com.rajavavapor.app.ui.modules.request.RequestFragment
// KasFragment -> moved to com.rajavavapor.app.ui.modules.kas.KasListFragment
// PiutangFragment -> moved to com.rajavavapor.app.ui.modules.piutang.PiutangFragment
// PembelianFragment -> moved to com.rajavavapor.app.ui.modules.pembelian.PembelianFragment
// PayrollFragment -> moved to com.rajavavapor.app.ui.modules.payroll.PayrollFragment
// IzinFragment -> moved to com.rajavavapor.app.ui.modules.izin.IzinFragment
// MonitoringFragment -> moved to com.rajavavapor.app.ui.modules.monitoring.MonitoringFragment
// LaporanFragment -> moved to com.rajavavapor.app.ui.modules.laporan.LaporanFragment
// CabangListFragment -> moved to com.rajavavapor.app.ui.modules.cabang.CabangListFragment
// UsersFragment -> moved to com.rajavavapor.app.ui.modules.users.UsersFragment
// PromoFragment -> moved to com.rajavavapor.app.ui.modules.promo.PromoFragment
