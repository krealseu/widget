package org.kreal.filepickdialog

import android.animation.Animator
import android.app.Dialog
import android.app.DialogFragment
import android.app.Fragment
import android.app.FragmentTransaction
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.lang.Thread.sleep

/**
 * Created by lthee on 2018/1/6.
 */
class EditDialog : DialogFragment(), View.OnClickListener {
    override fun onClick(button: View) {
        when ((button as Button).text) {
            positiveText -> {
                val result = editText.text.toString()
                if (result.isEmpty()) {
                    val animation = AnimationUtils.loadAnimation(activity, R.anim.shark)
                    animation.setInterpolator {
                        val num = 5
                        (Math.sin(it * num * 2 * Math.PI) * (Math.E - Math.exp(it.toDouble()))).toFloat()
                    }
                    editText.startAnimation(animation)
                } else {
                    success(result)
                    dismiss()
                }
            }
            negativeText -> {
                dialog.cancel()
            }
        }
    }

    var title: String? = null
    var tips: String? = null
    var text: String? = null
    var positiveText: String = "OK"
    var negativeText: String = "CANCEL"
    var cancel: () -> Unit = {}
    var success: (String) -> Unit = {}
    private lateinit var editText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val editDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LayoutInflater.from(context).inflate(R.layout.editdialog_layout, null)
        } else {
            LayoutInflater.from(activity).inflate(R.layout.editdialog_layout, null)
        }
        editText = editDialog.findViewById(R.id.editDialog)
        if (tips == null) {
            editDialog.findViewById<TextView>(R.id.tips).visibility = View.GONE
        } else editDialog.findViewById<TextView>(R.id.tips).text = tips
        return AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(editDialog)
                .setPositiveButton(positiveText, null)
                .setNegativeButton(negativeText, null)
                .create()
    }

    override fun onResume() {
        super.onResume()
        (dialog as AlertDialog).apply {
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this@EditDialog)
            getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(this@EditDialog)
            editText.setText(text)
            Thread {
                sleep(100)
                (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(editText, 0)
            }.start()
        }
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return super.show(transaction, tag)
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        cancel
    }
}