package zurmati.floating.widget

import android.os.Bundle
import android.content.Intent
import android.app.AlertDialog
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import zurmati.floating.widget.databinding.MainBinding
import java.text.SimpleDateFormat
import java.util.*

class Main : AppCompatActivity() {

    private val binding by lazy {
        MainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnStartService.setOnClickListener {
            floatingService(true)
        }
        binding.btnMsg.setOnClickListener {
            floatingService(false)
        }
    }

    private fun floatingService(isStartService: Boolean) {
        val code = if (isStartService)
            OVERLAY_PERMISSION_REQ_CODE_CHATHEAD
        else
            OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG

        if (!Utils.canDrawOverlays(this@Main))
            requestPermission(code)
        else if (isStartService)
            startChatHead()
        else
            showChatHeadMsg()

    }

    private fun startChatHead() {
        ContextCompat.startForegroundService(
            this@Main,
            Intent(this@Main, ChatHeadService::class.java)
        )
    }

    private fun showChatHeadMsg() {
        val now = Date()
        val str =
            "test by zurmati  " + SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(
                now
            )
        val it = Intent(this@Main, ChatHeadService::class.java)
        it.putExtra(Utils.EXTRA_MSG, str)
        ContextCompat.startForegroundService(this@Main, it)
    }

    private fun needPermissionDialog(requestCode: Int) {
        val builder = AlertDialog.Builder(this@Main)
        builder.setMessage("You need to allow this permission")
        builder.setPositiveButton(
            "OK"
        ) { dialog, _ ->
            dialog.dismiss()
            requestPermission(requestCode)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }


    private fun requestPermission(requestCode: Int) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD) {
            if (!Utils.canDrawOverlays(this@Main)) {
                needPermissionDialog(requestCode)
            } else {
                startChatHead()
            }
        } else if (requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG) {
            if (!Utils.canDrawOverlays(this@Main)) {
                needPermissionDialog(requestCode)
            } else {
                showChatHeadMsg()
            }
        }
    }



    companion object {
        var OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234
        var OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678
    }
}