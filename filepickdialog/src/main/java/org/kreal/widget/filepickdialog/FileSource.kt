package org.kreal.widget.filepickdialog

import android.net.Uri
import android.support.v4.provider.DocumentFile
import java.util.*

/**
 * Created by lthee on 2018/1/7.
 * 数据源
 */
internal class FileSource(documentFile: DocumentFile, var mineType: String = "*/*") {

    var workDir: DocumentFile = documentFile
        set(value) {
            if (value.isDirectory) {
                loadList(value)
                field = value
            }
        }

    val selectUri: MutableList<Uri> = arrayListOf()

    private var listFiles: Array<DocumentFile> = arrayOf()

    init {
        if (documentFile.isDirectory && documentFile.canRead()) {
            workDir = documentFile
        }
    }


    private fun loadList(file: DocumentFile): Array<DocumentFile> {
        listFiles = file.listFiles().filter {
            if (it.isDirectory)
                true
            else {
                val params = mineType.split('/')
                val types = it.type.split('/')
                if (params.size != 2 || types.size != 2)
                    false
                else {
                    var result = 0
                    if (params[0] != "*")
                        if (params[0] != types[0])
                            result++
                    if (params[1] != "*")
                        if (params[1] != types[1])
                            result++
                    result == 0
                }
            }
        }.toTypedArray()
        Arrays.sort(listFiles) { f1, f2 ->
            if (f1.isDirectory && f2.isFile)
                -1
            else if (f1.isFile && f2.isDirectory)
                1
            else f1.name.compareTo(f2.name, ignoreCase = true)
        }
        return listFiles
    }

    var size: Int = 0
        get() = listFiles.size
        private set

    fun cd(name: String): Boolean {
        val file = if (name != "..") workDir.findFile(name) ?: return false else workDir.parentFile
                ?: return false
        return if (file.isDirectory && file.canRead()) {
            workDir = file
//            loadList(file)
            true
        } else false
    }

    operator fun get(index: Int): DocumentFile = listFiles[index]

    fun getState(documentFile: DocumentFile): Int = if (selectUri.contains(documentFile.uri)) 1 else 0

    fun select(documentFile: DocumentFile) {
        if (selectUri.contains(documentFile.uri))
            selectUri.remove(documentFile.uri)
        else selectUri.add(documentFile.uri)
    }

}