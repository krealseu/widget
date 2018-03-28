package org.kreal.widget.qrshow

import android.app.DialogFragment
import android.app.FragmentManager
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

/**
 * Created by lthee on 2018/1/19.
 * QRActivity和QRDialog只提供显示image的imageView，并调用QRShow处理。
 */
internal class QRDialogFragment : DialogFragment(), View.OnLongClickListener {
    init {
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onLongClick(p0: View?): Boolean {
        Toast.makeText(activity, info, Toast.LENGTH_LONG).show()
        return true
    }

    private val key = "KEY_INFO"
    private lateinit var qrShow: QRShow
    private lateinit var imageView: ImageView
    var cancel: (() -> Unit) = {}
    var info: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        imageView = ImageView(activity)
        imageView.setOnLongClickListener(this)
        if (info == "")
            info = savedInstanceState?.getString(key) ?: ""
        qrShow = QRShow(imageView)
        return imageView
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
        if (fragment is QRDialogFragment)
            fragment.cancel = this.cancel
        else return super.show(manager, tag)
    }


    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        cancel()
    }
}