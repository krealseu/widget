package org.kreal.widget

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.kreal.widget.filepickdialog.FilePickDialogFragment
import org.kreal.widget.qrshow.QRShow

class MainActivity : AppCompatActivity(), View.OnClickListener {
    @SuppressLint("WifiManagerLeak")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.test_file_pick -> {
                FilePickDialogFragment().apply {
                    selectFolder = true
//                    multiSelect = true
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
//                val wifiConfig: WifiConfiguration = WifiConfiguration()
//                wifiConfig.SSID = "wokao"
//                wifiConfig.hiddenSSID = true
//                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
//                wifiConfig.preSharedKey = "123456789"
//                val wifiManager: WifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
//                wifiManager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.java).invoke(wifiManager, wifiConfig, true)

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_SETTINGS), 0)
        }
    }
}
