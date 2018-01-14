package org.kreal.widget

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.kreal.widget.filepickdialog.FilePickDialogFragment

class MainActivity : AppCompatActivity() {
    private lateinit var resultView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultView = result_view
        test_file_pick.setOnClickListener {
            FilePickDialogFragment().apply {
                selectFolder = true
                setListener {
                    var string: String = ""
                    it.forEach {
                        string += it
                    }
                    resultView.text = string
                }
            }.show(fragmentManager, "l")
        }
    }
}
