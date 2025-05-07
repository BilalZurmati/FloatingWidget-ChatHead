package zurmati.floating.widget

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.view.Window
import androidx.core.content.ContextCompat
import zurmati.floating.widget.databinding.DialogBinding

class MyDialog : Activity() {

    private val binding by lazy {
        DialogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        myDialog = this@MyDialog

        binding.dialogBtn.setOnClickListener {

            val str = binding.dialogEdt.text.toString()
            if (str.isNotEmpty()) {
//					ChatHeadService.showMsg(MyDialog.this, str);
                val serviceIntent = Intent(this@MyDialog, ChatHeadService::class.java)
                serviceIntent.putExtra(Utils.EXTRA_MSG, str)
                ContextCompat.startForegroundService(this@MyDialog, serviceIntent)
            }
        }
        binding.dialogTop.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        active = true
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun onDestroy() {
        super.onDestroy()
        active = false
    }

    companion object {
        var active = false

        var myDialog: Activity? = null
    }
}