package org.kreal.widget.qrshow

import android.app.Activity
import android.content.Context

/**
 * Created by lthee on 2018/1/20.
 * 对外使用的类
 */

class QRShow(private val context: Context) {
    infix fun show(info: String) {
        if (context is Activity) {
            QRDialogFragment().apply { this.info = info }.show(context.fragmentManager, "QR")
        } else {
            val intent = QRActivity.intent(context, info)
            context.startActivity(intent)
        }
    }
}