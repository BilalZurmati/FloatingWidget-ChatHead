package zurmati.floating.widget

import android.content.Context
import android.os.Build
import android.provider.Settings

object Utils {
    @JvmField
	var LogTag = "henrytest"
    @JvmField
	var EXTRA_MSG = "extra_msg"
    fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            Settings.canDrawOverlays(context)
        }
    }
}