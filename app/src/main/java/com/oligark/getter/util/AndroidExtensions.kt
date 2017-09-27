@file:JvmName("AndroidUtils")

package com.oligark.getter.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

/**
 * Created by pmvb on 17-09-19.
 */
fun View.animate(visibility: Int, alpha: Float, duration: Long) {
    val show = visibility == View.VISIBLE
    if (show) {
        this.alpha = 0f
    }
    this.visibility = View.VISIBLE
    this.animate()
            .setDuration(duration)
            .alpha(if (show) alpha else 0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    this@animate.visibility = visibility
                }
            })
}