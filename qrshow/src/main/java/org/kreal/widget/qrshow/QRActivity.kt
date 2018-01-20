package org.kreal.widget.qrshow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager

class QRActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        try {
            val info = intent.getStringExtra(INFO)
            QRDialogFragment().apply {
                this.info = info
                cancel = {
                    finish()
                }
            }.show(fragmentManager, "QR")
        } catch (e: Exception) {
            throw Error("error intent")
        }
    }

    companion object {
        private val INFO = "QRActivity_info"

        fun intent(context: Context, info: String): Intent {
            val intent = Intent(context, QRActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent.putExtra(INFO, info)
        }
    }
}
