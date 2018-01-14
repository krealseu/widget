package org.kreal.widget.filepickdialog

import android.util.Log
import java.io.File
import java.util.*

/**
 * Created by lthee on 2018/1/7.
 */
class FileSource(path: String, private val mineType: String) {

    var workDir: File

    val selectedFile = ArrayList<String>()

    private var lists: Array<File> = arrayOf()

    init {
        val file = File(path)
        workDir = file
        if (file.isDirectory && file.canRead()) {
            loadList(file)
        }
    }

    private fun loadList(folder: File) {
        lists = folder.listFiles { file ->
            !file.name.startsWith(".")
        }
        lists.sortWith(Comparator { f1, f2 ->
            if (f1.isDirectory && f2.isFile)
                -1
            else if (f1.isFile && f2.isDirectory)
                1
            else f1.name.compareTo(f2.name, ignoreCase = true)
        })
    }

    var size: Int = 0
        get() = lists.size
        private set

    fun cd(name: String) {
        val file = if (name != "..") File(workDir, name) else workDir.parentFile
        if (file.isDirectory && file.canRead()) {
            workDir = file
            loadList(file)
        }
    }

    fun getIndex(index: Int): File = lists[index]

    fun getIndexState(index: Int): Boolean = isSelected(lists[index].path)

    fun singleSelect(i: Int) {
        val value = lists[i].path
        val newState = !isSelected(value)
        selectedFile.removeAll { true }
        if (newState) selectedFile.add(value)
    }

    fun multiSelect(i: Int) {
        val value = lists[i].path
        if (isSelected(value)) selectedFile.remove(value) else selectedFile.add(value)
    }

    private fun isSelected(filePath: String) = selectedFile.contains(filePath)

}