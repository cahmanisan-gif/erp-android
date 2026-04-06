package com.rajavavapor.app.util

import android.animation.ValueAnimator
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import java.text.NumberFormat
import java.util.Locale

object AnimationHelper {

    /**
     * Animate a number from 0 to target value with Rupiah formatting.
     * Used on dashboard for omzet, cash, etc.
     */
    fun animateRupiah(textView: TextView, targetValue: Double, duration: Long = 800) {
        val formatter = NumberFormat.getInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }

        ValueAnimator.ofFloat(0f, targetValue.toFloat()).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                val value = (animation.animatedValue as Float).toDouble()
                textView.text = "Rp ${formatter.format(value)}"
            }
            start()
        }
    }

    /**
     * Animate an integer counter from 0 to target.
     */
    fun animateCounter(textView: TextView, targetValue: Int, duration: Long = 600) {
        ValueAnimator.ofInt(0, targetValue).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                textView.text = (animation.animatedValue as Int).toString()
            }
            start()
        }
    }

    /**
     * Animate counter with suffix text (e.g., "24 transaksi").
     */
    fun animateCounterWithSuffix(textView: TextView, targetValue: Int, suffix: String, duration: Long = 600) {
        ValueAnimator.ofInt(0, targetValue).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                textView.text = "${animation.animatedValue as Int} $suffix"
            }
            start()
        }
    }

    /**
     * Perform haptic feedback on view click.
     * Call this in button click listeners for tactile response.
     */
    fun hapticClick(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    /**
     * Scale bounce animation on click.
     * Makes buttons feel responsive.
     */
    fun bounceClick(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(80)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(80)
                    .start()
            }
            .start()
    }
}
