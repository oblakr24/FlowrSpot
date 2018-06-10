package dev.rokoblak.flowrspot.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

/**
 * Fades in a view from a previous one
 */
fun View.crossFadeFrom(previous: View, fadeDuration: Long = 800L) {
    visibility = View.VISIBLE
    previous.visibility = View.VISIBLE

    val newView = this
    if (this == previous) return

    if (fadeDuration == 0L) {
        previous.visibility = View.GONE
    } else {

        val fadeOutDurationFactor = 0.7f

        animation = AlphaAnimation(0f, 1f).apply {
            interpolator = DecelerateInterpolator()
            duration = fadeDuration
        }

        previous.animation = AlphaAnimation(1f, 0f).apply {
            interpolator = AccelerateInterpolator()
            duration = (fadeDuration * fadeOutDurationFactor).toLong()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    newView.visibility = View.VISIBLE
                    previous.visibility = View.GONE
                }
            })
        }
    }
}