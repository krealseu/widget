package org.kreal.widget.qrshow

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * Created by lthee on 2018/1/20.
 * 对外使用的类
 * 生成QR图片的工作全交给QRShow处理,由于使用了AsyncTask异步处理，需要在UI线程中调用show()
 * QRShow支持将生成图片绑定到指定imageView。
 */

class QRShow private constructor(private val context: Context? = null, private val imageView: ImageView? = null) {
    constructor(context: Context) : this(context, null)

    constructor(imageView: ImageView) : this(null, imageView)

    constructor() : this(null, null)

    private var loadTask: LoadTask? = null

    private var width: Int = 800

    private var height: Int = 800

    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun getState(): AsyncTask.Status = loadTask?.status ?: AsyncTask.Status.PENDING

    fun get(): Bitmap? = loadTask?.get()

    fun get(timeout: Long, unit: TimeUnit): Bitmap? = loadTask?.get(timeout, unit)

    infix fun show(info: String): QRShow {
        when {
            context is Activity -> QRDialogFragment().apply { this.info = info }.show(context.fragmentManager, "QR")
            context is Fragment -> QRDialogFragment().apply { this.info = info }.show(context.fragmentManager, "QR")
            context != null -> context.startActivity(QRActivity.intent(context, info))
            loadTask == null -> {
                loadTask = LoadTask(imageView, width, height)
                loadTask?.also {
                    when (it.status) {
                        AsyncTask.Status.PENDING -> it.execute(info)
                        AsyncTask.Status.FINISHED -> imageView?.setImageBitmap(it.get())
                        else -> Unit
                    }
                }
            }
        }
        return this
    }

    class LoadTask(imageView: ImageView? = null, private val width: Int, private val height: Int) : AsyncTask<String, Void, Bitmap>() {

        private val imageView: WeakReference<ImageView?> = WeakReference(imageView)
        private lateinit var progressBar: WeakReference<ProgressBar>
        private lateinit var viewGroup: WeakReference<ViewGroup?>
        private var imageViewIndex: Int = 0
        private lateinit var imageViewLayoutParams: ViewGroup.LayoutParams
        override fun doInBackground(vararg info: String): Bitmap? {
            if (isCancelled)
                return null
//            Thread.sleep(1000)
            return QRShow.getInfoBitmap(info[0], width, height)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            imageView.get()?.also {
                progressBar = WeakReference(ProgressBar(it.context))
                viewGroup = WeakReference(if (it.parent == null) null else it.parent as ViewGroup)
                imageViewIndex = viewGroup.get()?.indexOfChild(it) ?: 0
                imageViewLayoutParams = it.layoutParams ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                viewGroup.get()?.also {
                    it.removeView(imageView.get())
                    it.addView(progressBar.get(), imageViewIndex, imageViewLayoutParams)
                }
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            imageView.get()?.also {
                it.setImageBitmap(result)
                viewGroup.get()?.let {
                    it.removeView(progressBar.get())
                    it.addView(imageView.get(), imageViewIndex)
                }
            }
        }
    }

    companion object {
        fun getInfoBitmap(info: String, width: Int = 800, height: Int = 800): Bitmap {
            return bitMatrixToBitmap(MultiFormatWriter().encode(info, BarcodeFormat.QR_CODE, width, height))
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