package org.kreal.filepickdialog

import android.Manifest
import android.app.DialogFragment
import android.app.FragmentManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.File

/**
 * Created by lthee on 2018/1/6.
 */
class FilePickDialogFragment : DialogFragment(), FileAdapter.OnItemClickListener, View.OnClickListener {
    override fun onClick(view: View) {
        when (view.id) {
            R.id.cancel_button -> dialog.cancel()
            R.id.newFile_button -> EditDialog().apply {
                title = "Create Folder"
                success = {
                    val file = File(fileSource.workDir, it)
                    if (!file.exists())
                        if (file.mkdir()) {
                            fileSource.workDir = file
                            folderName.text = fileSource.workDir.name
                            fileAdapt.notifyDataSetChanged()
                        } else Unit
                    else if (file.isDirectory) {
                        fileSource.cd(it)
                        folderName.text = fileSource.workDir.name
                        fileAdapt.notifyDataSetChanged()
                    }
                }
            }.show(fragmentManager, "Create Folder")
            R.id.select_button -> {
                if (selectFolder)
                    listener?.select(fileSource.workDir.absolutePath)
                else listener?.select(*fileSource.selectedFile.toTypedArray())
                dismiss()
            }
            R.id.folder_back -> {
                fileSource.cd("..")
                folderName.text = fileSource.workDir.name
                fileAdapt.notifyDataSetChanged()
            }
            R.id.go_setting_button -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + activity.packageName)
                startActivityForResult(intent, 424)
            }
            R.id.grant_button -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermissions(permissions, 233)
        }
    }

    override fun onItemClick(position: Int) {
        val file = fileSource.getIndex(position)
        if (file.isDirectory) {
            fileSource.cd(file.name)
            fileAdapt.notifyDataSetChanged()
            folderName.text = fileSource.workDir.name
        } else if (!selectFolder) {
            if (file.isFile) {
                if (multiSelect) {
                    fileSource.multiSelect(position)
                    fileAdapt.notifyItemChanged(position)
                } else {
                    fileSource.singleSelect(position)
                    fileAdapt.notifyItemRangeChanged(0, fileSource.size)
                }
            }
        }

    }

    private lateinit var filePickLayout: RelativeLayout
    private lateinit var permissionAskLayout: LinearLayout
    private lateinit var recycleView: RecyclerView
    private lateinit var cancelButton: Button
    private lateinit var selectButton: Button
    private lateinit var createButton: Button
    private lateinit var listener: OnSelectListener
    private lateinit var folderName: TextView
    private lateinit var folderBack: ImageButton
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    var selectFolder = false
    var defaultPath: String = Environment.getExternalStorageDirectory().path
    var miniType = "*/*"
    var multiSelect = false

    private val fileSource = FileSource(defaultPath, miniType)
    private val fileAdapt: FileAdapter = FileAdapter(fileSource, this)

    var cancel: () -> Unit = {}

    fun setListener(l: OnSelectListener) {
        listener = l
    }

    fun setListener(lf: (result: Array<out String>) -> Unit) {
        listener = object : OnSelectListener {
            override fun select(vararg result: String) {
                lf(result)
            }
        }
    }

    private fun checkPermissions(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true
        permissions.forEach {
            if (context.checkSelfPermission(it) == PackageManager.PERMISSION_DENIED)
                return false
        }
        return true
    }

    private fun showView() {
        if (checkPermissions(activity)) {
            fileSource.cd("")
            fileAdapt.notifyDataSetChanged()
            filePickLayout.visibility = View.VISIBLE
            permissionAskLayout.visibility = View.INVISIBLE
        } else {
            filePickLayout.visibility = View.INVISIBLE
            permissionAskLayout.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_dialog, container)
        view.also {
            filePickLayout = it.findViewById(R.id.file_pick_layout)
            permissionAskLayout = it.findViewById(R.id.permission_ask)
            recycleView = it.findViewById(R.id.fileList)
            cancelButton = it.findViewById(R.id.cancel_button)
            selectButton = it.findViewById(R.id.select_button)
            createButton = it.findViewById(R.id.newFile_button)
            folderName = it.findViewById(R.id.folder_name)
            folderBack = it.findViewById(R.id.folder_back)

            cancelButton.setOnClickListener(this)
            selectButton.setOnClickListener(this)
            createButton.setOnClickListener(this)
            folderBack.setOnClickListener(this)
            it.findViewById<Button>(R.id.go_setting_button).setOnClickListener(this)
            it.findViewById<Button>(R.id.grant_button).setOnClickListener(this)

            recycleView.adapter = fileAdapt
            recycleView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recycleView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        showView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        showView()
    }

    override fun onResume() {
        super.onResume()
        showView()
        if (!selectFolder) {
            createButton.visibility = View.INVISIBLE
        }
        folderName.text = fileSource.workDir.name
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        cancel
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (tag == null || manager.findFragmentByTag(tag) == null)
            return super.show(manager, tag)
    }

    interface OnSelectListener {
        fun select(vararg result: String)
    }

}