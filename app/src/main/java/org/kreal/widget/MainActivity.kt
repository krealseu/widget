package org.kreal.widget

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.kreal.widget.filepickdialog.FilePickDialogFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FilePickDialogFragment().apply {
            selectFolder = true
            setListener {
                it.forEach {
                    Log.i("jj", it)
                }
            }
        }.show(fragmentManager, "l")
    }
}
