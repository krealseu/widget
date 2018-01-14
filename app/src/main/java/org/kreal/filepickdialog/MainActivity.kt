package org.kreal.filepickdialog

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log

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
