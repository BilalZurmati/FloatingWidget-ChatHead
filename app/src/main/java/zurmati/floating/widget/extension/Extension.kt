package zurmati.floating.widget.extension

import android.animation.ValueAnimator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator

fun WindowManager?.updateViewAt(view: View?, params: WindowManager.LayoutParams) {
    if (view?.isAttachedToWindow == true) {
        this?.updateViewLayout(view, params)
    }
}

fun Vibrator?.vibrateNow() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this?.vibrate(
            VibrationEffect.createOneShot(
                50, // duration in milliseconds
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        // Deprecated in API 26
        this?.vibrate(50)
    }
}

fun WindowManager?.animateViewTo(
    view: View?,
    startX: Int,
    startY: Int,
    endX: Int,
    endY: Int,
    layoutParams: WindowManager.LayoutParams
) {
    val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 50 // Slightly longer than 50ms for better smoothness
        interpolator = AccelerateDecelerateInterpolator() // Smooth start & end

        addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            layoutParams.x = (startX + (endX - startX) * progress).toInt()
            layoutParams.y = (startY + (endY - startY) * progress).toInt()
            this@animateViewTo?.updateViewAt(view, layoutParams)
        }
    }
    animator.start()
}
