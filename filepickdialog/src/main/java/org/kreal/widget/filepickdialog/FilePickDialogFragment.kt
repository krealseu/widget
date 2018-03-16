package org.kreal.widget.filepickdialog

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
import android.support.v4.provider.DocumentFile
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.kreal.storage.Storage
import java.io.File
import java.util.*

/**
 * Created by lthee on 2018/1/6.
 * FilePickDialogFragment
 */
class FilePickDialogFragment : DialogFragment(), FileAdapter.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        history.clear()
        if (!storage.isGrant(storage.devs[position])) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, 22)
        } else {
            history.clear()
            val dev = storage.devs[position]
            fileSource.workDir = if (dev == "sdcard") DocumentFile.fromFile(File(defaultPath)) else DocumentFile.fromFile(File("/storage/$dev"))
            fileAdapt.notifyDataSetChanged()
            folderName.text = fileSource.workDir.name
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cancel_button -> dialog.cancel()
            R.id.newFile_button -> EditDialog().apply {
                title = "Create Folder"
                success = { name->
                    val file = fileSource.workDir.createDirectory(name)
                    file?.let {
                        if (it.isDirectory) {
                            if (fileSource.cd(name)) {
                                val position = linearLayoutManager.findFirstVisibleItemPosition()
                                val offset = linearLayoutManager.getChildAt(0)?.top ?: 0
                                val result = position to offset
                                history.push(result)
                                folderName.text = fileSource.workDir.name
                                fileAdapt.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }.show(fragmentManager, "Create Folder")
            R.id.select_button -> {
                if (selectFolder)
                    listener.select(fileSource.workDir.uri)
                else listener.select(*fileSource.selectUri.toTypedArray())
                dismiss()
            }
            R.id.folder_back -> {
                if (fileSource.cd("..")) {
                    val result = if (!history.isEmpty()) history.pop() else 0 to 0
                    folderName.text = fileSource.workDir.name
                    fileAdapt.notifyDataSetChanged()
                    linearLayoutManager.scrollToPositionWithOffset(result.first, result.second)
                }
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
            if (fileSource.cd(file.name)) {
                val lastPosition = linearLayoutManager.findFirstVisibleItemPosition()
                val offset = linearLayoutManager.getChildAt(0)?.top ?: 0
                val result = lastPosition to offset
                history.push(result)
                fileAdapt.notifyDataSetChanged()
                folderName.text = fileSource.workDir.name
            }
        } else if (!selectFolder) {
            if (file.isFile) {
                fileSource.select(file)
                if (multiSelect)
                    fileAdapt.notifyItemChanged(position)
                else
                    fileAdapt.notifyDataSetChanged()
                numView.text = fileSource.selectUri.size.toString()

            }
        }

    }

    private lateinit var filePickLayout: RelativeLayout
    private lateinit var permissionAskLayout: LinearLayout
    private lateinit var recycleView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var cancelButton: Button
    private lateinit var selectButton: Button
    private lateinit var createButton: Button
    private lateinit var listener: OnSelectListener
    private lateinit var folderName: TextView
    private lateinit var folderBack: ImageButton
    private lateinit var storageSelect: Spinner
    private lateinit var numView: TextView
    private lateinit var storage: Storage
    private val history: Stack<Pair<Int, Int>> = Stack()
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    var selectFolder = false
    var defaultPath: String = Environment.getExternalStorageDirectory().path
    var miniType = "*/*"
        set(value) {
            fileSource.mineType = value
            field = value
        }
    var multiSelect = false
        set(value) {
            fileSource.multiSelect = value
            field = value
        }

    private val fileSource = FileSource(DocumentFile.fromFile(File(defaultPath)))
    private val fileAdapt: FileAdapter = FileAdapter(fileSource, this)

    var cancel: () -> Unit = {}

    fun setListener(l: OnSelectListener) {
        listener = l
    }

    fun setListener(lf: (result: Array<out Uri>) -> Unit) {
        listener = object : OnSelectListener {
            override fun select(vararg result: Uri) {
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
        storage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Storage(context) else Storage(activity)
        view.also {
            filePickLayout = it.findViewById(R.id.file_pick_layout)
            permissionAskLayout = it.findViewById(R.id.permission_ask)
            recycleView = it.findViewById(R.id.fileList)
            cancelButton = it.findViewById(R.id.cancel_button)
            selectButton = it.findViewById(R.id.select_button)
            createButton = it.findViewById(R.id.newFile_button)
            folderName = it.findViewById(R.id.folder_name)
            folderBack = it.findViewById(R.id.folder_back)
            storageSelect = it.findViewById(R.id.sp)
            numView = it.findViewById(R.id.num_select)

            storageSelect.adapter = ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, storage.devs)
            storageSelect.onItemSelectedListener = this
            cancelButton.setOnClickListener(this)
            selectButton.setOnClickListener(this)
            createButton.setOnClickListener(this)
            folderBack.setOnClickListener(this)
            it.findViewById<Button>(R.id.go_setting_button).setOnClickListener(this)
            it.findViewById<Button>(R.id.grant_button).setOnClickListener(this)

            recycleView.adapter = fileAdapt
            linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recycleView.layoutManager = linearLayoutManager
            recycleView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        showView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val path = data.data.path
        if (path.startsWith("/tree") and path.endsWith(':')) {
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) context else activity).contentResolver.takePersistableUriPermission(data.data, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            storage.reload()
        }
        showView()
    }

    override fun onResume() {
        super.onResume()
        showView()
        if (!selectFolder) {
            createButton.visibility = View.INVISIBLE
            numView.visibility = View.VISIBLE
            numView.text = fileSource.selectUri.size.toString()
        } else {
            createButton.visibility = View.VISIBLE
            numView.visibility = View.GONE
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
        fun select(vararg result: Uri)
    }

}