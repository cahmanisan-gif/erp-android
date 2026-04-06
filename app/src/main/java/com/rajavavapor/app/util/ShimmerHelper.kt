package com.rajavavapor.app.util

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.facebook.shimmer.ShimmerFrameLayout
import com.rajavavapor.app.R

/**
 * Helper to add shimmer loading overlay to any ViewGroup.
 * Usage:
 *   val shimmer = ShimmerHelper.addTo(binding.root)
 *   shimmer.show()  // show shimmer, hide content
 *   shimmer.hide()  // hide shimmer, show content
 */
class ShimmerHelper private constructor(
    private val shimmerView: ShimmerFrameLayout,
    private val contentParent: ViewGroup
) {
    fun show() {
        shimmerView.isVisible = true
        shimmerView.startShimmer()
    }

    fun hide() {
        shimmerView.stopShimmer()
        shimmerView.isVisible = false
    }

    companion object {
        /**
         * Inflates shimmer_list.xml and adds it as an overlay to the parent.
         * The parent must be a FrameLayout or similar that supports overlapping children.
         * For SwipeRefreshLayout, add to its child FrameLayout.
         */
        fun create(parent: ViewGroup): ShimmerHelper {
            val shimmer = LayoutInflater.from(parent.context)
                .inflate(R.layout.shimmer_list, parent, false) as ShimmerFrameLayout
            parent.addView(shimmer)
            return ShimmerHelper(shimmer, parent)
        }
    }
}
