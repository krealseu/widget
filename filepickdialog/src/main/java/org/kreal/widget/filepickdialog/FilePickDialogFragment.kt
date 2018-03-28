package org.kreal.widget.filepickdialog

import android.Manifest
import android.app.AlertDialog
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
import java.util.*

/**
 * Created by lthee on 2018/1/6.
 * FilePickDialogFragment
 */
class FilePickDialogFragment : DialogFragment(),
        FileAdapter.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    init {
        setStyle(STYLE_NO_TITLE, 0)
        retainInstance = true
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        if (fileSource.workDir.uri.path.startsWith(storage.getVolumes()[position].path))
            return
        history.clear()
        fileSource.workDir = storage.getDocumentFile(storage.getAvailableVolumes()[position].path) ?: return
        fileAdapt.notifyDataSetChanged()
        folderName.text = fileSource.workDir.name

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.go_setting_button -> startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.packageName)), 424)
            R.id.grant_button -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermissions(permissions, 233)
            R.id.cancel_button -> dismiss()
            R.id.newFile_button ->
                EditDialog().apply {
                    title = "Create Folder"
                    success = { name ->
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
                isSelect = true
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
        }
    }

    override fun onItemClick(data: DocumentFile, position: Int) {
        if (data.isDirectory) {
            if (fileSource.cd(data.name)) {
                val lastPosition = linearLayoutManager.findFirstVisibleItemPosition()
                val offset = linearLayoutManager.getChildAt(0)?.top ?: 0
                val result = lastPosition to offset
                history.push(result)
                fileAdapt.notifyDataSetChanged()
                folderName.text = fileSource.workDir.name
            }
        } else when (type) {
            MULTI_FILE_PICK -> {
                fileSource.select(data)
                fileAdapt.notifyItemChanged(position)
                numView.text = fileSource.selectUri.size.toString()
            }
            FILE_PICK -> {
                fileSource.selectUri.clear()
                fileSource.select(data)
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
    private var isSelect: Boolean = false
    private val history: Stack<Pair<Int, Int>> = Stack()
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val fileSource = FileSource(DocumentFile.fromFile(Environment.getExternalStorageDirectory()))
    private val fileAdapt: FileAdapter = FileAdapter(fileSource, this)

    var type: Int = 1
        set(value) {
            field = when (value) {
                DIRECTORY_CHOOSE, MULTI_FILE_PICK -> value
                else -> FILE_PICK
            }
        }

    var miniType: String = "*/*"
        set(value) {
            fileSource.mineType = value
            field = value
        }


    fun setListener(l: OnSelectListener) {
        listener = l
    }

    fun setListener(lf: (result: Array<out Uri>) -> Unit) {
        listener = object : OnSelectListener {
            override fun onSelect(vararg result: Uri) {
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

    /**
     * 判断选择显示授权界面还是主界面
     */
    private fun showView() {
        if (checkPermissions(activity)) {
            fileAdapt.notifyDataSetChanged()
            filePickLayout.visibility = View.VISIBLE
            permissionAskLayout.visibility = View.GONE
        } else {
            filePickLayout.visibility = View.GONE
            permissionAskLayout.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = Storage(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) context else activity)
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
            storageSelect = it.findViewById(R.id.sp)
            numView = it.findViewById(R.id.num_select)
            val volumes = storage.getAvailableVolumes()
            storageSelect.adapter = ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, Array(volumes.size) { volumes[it].uuid })
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
        this.showView()
    }

    override fun onResume() {
        super.onResume()
        this.showView()
        when (type) {
            DIRECTORY_CHOOSE -> {
                createButton.visibility = View.VISIBLE
                numView.visibility = View.GONE
            }
            else -> {
                createButton.visibility = View.INVISIBLE
                numView.visibility = View.VISIBLE
            }
        }
        numView.text = fileSource.selectUri.size.toString()
        folderName.text = fileSource.workDir.name
    }

    override fun show(manager: FragmentManager, tag: String) {
        val fragment = manager.findFragmentByTag(tag)
        if (fragment is FilePickDialogFragment)
            fragment.listener = this.listener
        else super.show(manager, tag)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        if (isSelect) when (type) {
            DIRECTORY_CHOOSE -> listener.onSelect(fileSource.workDir.uri)
            else -> listener.onSelect(*fileSource.selectUri.toTypedArray())
        }
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance)
            dialog.setDismissMessage(null)
        super.onDestroyView()
    }

    companion object {
        const val FILE_PICK: Int = 1
        const val DIRECTORY_CHOOSE: Int = 2
        const val MULTI_FILE_PICK: Int = 3
    }

    interface OnSelectListener {
        fun onSelect(vararg result: Uri)
    }

}