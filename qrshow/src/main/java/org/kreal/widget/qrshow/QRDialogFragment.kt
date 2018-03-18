package org.kreal.widget.qrshow

import android.app.DialogFragment
import android.app.FragmentManager
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

/**
 * Created by lthee on 2018/1/19.
 * QRActivity和QRDialog只提供显示image的imageView，并调用QRShow处理。
 */
class QRDialogFragment : DialogFragment(), View.OnLongClickListener {
    override fun onLongClick(p0: View?): Boolean {
//        val clipData = ClipData.newPlainText("QR_Info", info)
//        val clipboard: ClipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        clipboard.primaryClip = clipData
        Toast.makeText(activity, info, Toast.LENGTH_LONG).show()
        return true
    }

    private val key = "KEY_INFO"
    private lateinit var qrShow: QRShow
    private lateinit var imageView: ImageView
    var cancel: (() -> Unit) = {}
    var info: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.qr_layout, container, false)
        imageView = view.findViewById(R.id.qr)
        imageView.setOnLongClickListener(this)
        if (info == "")
            info = savedInstanceState?.getString(key) ?: ""
        qrShow = QRShow(imageView)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (qrShow.getState() == AsyncTask.Status.PENDING) {
            if (info == "") {
                Toast.makeText(activity, "Empty message", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                qrShow show info
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.also {
            if (info != "")
                outState.putString(key, info)
        }
    }

    override fun show(manager: FragmentManager, tag: String) {
        val fragment = manager.findFragmentByTag(tag)
        if (fragment == null)
            return super.show(manager, tag)
        else
            (fragment as QRDialogFragment).cancel = this.cancel
    }

    override fun dismiss() {
        super.dismiss()
        cancel()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        cancel()
    }
}