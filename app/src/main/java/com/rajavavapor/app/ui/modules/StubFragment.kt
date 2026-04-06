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

class PosFragment : StubFragment() { override val moduleName = "POS / Kasir" }
class InvoiceFragment : StubFragment() { override val moduleName = "Invoice" }
class CustomerFragment : StubFragment() { override val moduleName = "Customer" }
class ProdukFragment : StubFragment() { override val moduleName = "Produk" }
class ReturFragment : StubFragment() { override val moduleName = "Retur" }
class RequestFragment : StubFragment() { override val moduleName = "Request Produk" }
class KasFragment : StubFragment() { override val moduleName = "Kas / Bank" }
class PiutangFragment : StubFragment() { override val moduleName = "Piutang" }
class PembelianFragment : StubFragment() { override val moduleName = "Pembelian" }
class PayrollFragment : StubFragment() { override val moduleName = "Payroll" }
class IzinFragment : StubFragment() { override val moduleName = "Izin / Cuti" }
class MonitoringFragment : StubFragment() { override val moduleName = "Monitoring" }
class LaporanFragment : StubFragment() { override val moduleName = "Laporan Keuangan" }
class CabangFragment : StubFragment() { override val moduleName = "Cabang" }
class UsersFragment : StubFragment() { override val moduleName = "User Management" }
class PromoFragment : StubFragment() { override val moduleName = "Promo" }
