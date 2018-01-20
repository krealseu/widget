package org.kreal.widget.qrshow

import android.app.DialogFragment
import android.app.FragmentManager
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

/**
 * Created by lthee on 2018/1/19.
 * 显示QR图片的对话框Fragment
 */
class QRDialogFragment : DialogFragment(), View.OnLongClickListener {
    override fun onLongClick(p0: View?): Boolean {
//        val clipData = ClipData.newPlainText("QR_Info", info)
//        val clipboard: ClipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        clipboard.primaryClip = clipData
        Toast.makeText(activity, info, Toast.LENGTH_LONG).show()
        return true
    }

    private val KEY = "KEY_INFO"
    var info: String = ""
    private var hasCreate = false
    private val loadTask = LoadTask()
    private lateinit var imageView: ImageView
    private lateinit var waitView: ProgressBar
    var cancel: (() -> Unit) = {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.qr_layout, container, false)
        imageView = view.findViewById(R.id.qr)
        imageView.setOnLongClickListener(this)
        waitView = view.findViewById(R.id.progressBar)
        if (info == "")
            info = savedInstanceState?.getString(KEY) ?: ""
        return view
    }

    override fun onResume() {
        super.onResume()
        if (!hasCreate) {
            if (info == "") {
                Toast.makeText(activity, "Empty message", Toast.LENGTH_SHORT).show()
                dismiss()
            } else
                loadTask.execute(info)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.also {
            if (info != "")
                outState.putString(KEY, info)
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

    inner class LoadTask : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg info: String): Bitmap? {
            if (isCancelled)
                return null
//            Thread.sleep(1000)
            return bitMatrixToBitmap(MultiFormatWriter().encode(info[0], BarcodeFormat.QR_CODE, 800, 800))
        }

        override fun onPreExecute() {
            super.onPreExecute()
            imageView.visibility = View.INVISIBLE
            waitView.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            imageView.setImageBitmap(result)
            imageView.visibility = View.VISIBLE
            waitView.visibility = View.INVISIBLE
            hasCreate = true
        }

        private fun bitMatrixToBitmap(bitMatrix: BitMatrix): Bitmap {
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until width) {
                for (x in 0 until height) {
                    pixels[y * width + x] = if (bitMatrix.get(x, y)) -0x1000000 else -0x1 // black pixel
                }
            }
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bmp.setPixels(pixels, 0, width, 0, 0, width, height)
            return bmp
        }
    }
}