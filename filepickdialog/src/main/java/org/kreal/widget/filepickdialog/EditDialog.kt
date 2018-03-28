package org.kreal.widget.filepickdialog

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.lang.Thread.sleep
import kotlin.math.exp
import kotlin.math.sin

/**
 * Created by lthee on 2018/1/6.
 * 文件名编辑对话框
 */
internal class EditDialog : DialogFragment(), View.OnClickListener {

    override fun onClick(button: View) {
        when ((button as Button).text) {
            positiveText -> {
                val result = editText.text.toString()
                if (result.isEmpty()) {
                    val animation = TranslateAnimation(0f, 10f, 0f, 0f).apply {
                        duration = 800
                        setInterpolator { sin(it * 6 * 2 * 3.14159265358979323846f) * (2.7182818284590452354f - exp(it)) }
                    }
                    editText.startAnimation(animation)
                } else {
                    this.result = result
                    dismiss()
                }
            }
            negativeText -> dismiss()
        }
    }

    var title: String? = null
    var tips: String? = null
    var text: String? = null
    private val positiveText: String = "OK"
    private val negativeText: String = "CANCEL"
    var success: (String) -> Unit = {}

    private lateinit var editText: EditText
    private var result: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        savedInstanceState?.also {
            title = it.getString("Title")
        }

        return AlertDialog.Builder(activity)
                .setTitle(title)
                .setPositiveButton(positiveText, null)
                .setNegativeButton(negativeText, null)
                .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        savedInstanceState?.also {
            tips = it.getString("Tips")
            text = it.getString("Text")
        }
        val editDialog = inflater.inflate(R.layout.editdialog_layout, container, false)
        editText = editDialog.findViewById(R.id.editDialog)
        if (tips == null) {
            editDialog.findViewById<TextView>(R.id.tips).visibility = View.GONE
        } else editDialog.findViewById<TextView>(R.id.tips).text = tips
        (dialog as AlertDialog).setView(editDialog)
        return null
    }

    override fun onResume() {
        super.onResume()
        (dialog as AlertDialog).apply {
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this@EditDialog)
            getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(this@EditDialog)
            editText.setText(text)
            editText.setSelection(text?.length ?: 0)
            editText.selectionEnd
            Thread {
                sleep(100)
                (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(editText, 0)
            }.start()
        }
    }

    override fun onPause() {
        super.onPause()
        text = editText.text.toString()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.also {
            it.putString("Title", title)
            it.putString("Tips", tips)
            it.putString("Text", text)
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        if (result != "") success(result)
    }

}