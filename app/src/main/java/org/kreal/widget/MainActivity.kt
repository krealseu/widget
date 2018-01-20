package org.kreal.widget

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.kreal.widget.filepickdialog.FilePickDialogFragment
import org.kreal.widget.qrshow.QRDialogFragment
import org.kreal.widget.qrshow.QRShow

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(view: View) {
        when (view.id) {
            R.id.test_file_pick -> {
                FilePickDialogFragment().apply {
                    selectFolder = true
                    setListener {
                        var string = ""
                        it.forEach {
                            string += it
                        }
                        resultView.text = string
                    }
                }.show(fragmentManager, "l")
            }
            R.id.test_qr -> {
                QRShow(this) show "sdfadfadf"
            }
        }
    }

    private lateinit var resultView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultView = result_view
        test_file_pick.setOnClickListener(this)
        test_qr.setOnClickListener(this)
    }
}
