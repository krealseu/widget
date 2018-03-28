package org.kreal.widget.filepickdialog

import android.content.ContentResolver
import android.content.Context
import android.os.Environment
import android.os.storage.StorageManager
import android.support.v4.provider.DocumentFile
import java.io.File

/**
 * Created by lthee on 2018/3/13.
 * 统一的文件路径
 */
class Storage(private val context: Context) {
    private val grantUris: MutableMap<String, DocumentFile> = HashMap()
    private val volumeList: MutableList<Volume> = arrayListOf()

    init {
        loadGrantUri(context.contentResolver)
        volumeList.addAll(loadVolume(context))
    }

    private fun loadGrantUri(contentResolver: ContentResolver) {
        grantUris.clear()
        grantUris["sdcard"] = DocumentFile.fromFile(Environment.getExternalStorageDirectory())
        contentResolver.persistedUriPermissions.forEach {
            val path = it.uri.path
            if (path.startsWith("/tree/") and path.endsWith(":")) {
                val key = path.substring(6, path.length - 1)
                grantUris[key] = DocumentFile.fromTreeUri(context, it.uri)
            }
        }
    }

    fun getDocumentFile(path: String, canWrite: Boolean = false): DocumentFile? {
        if (canWrite) {
            volumeList.forEach { volume ->
                if (path.startsWith(volume.path) || path.startsWith("/${volume.uuid}")) {
                    if (volume.uuid == "sdcard")
                        return DocumentFile.fromFile(File(path))
                    else grantUris[volume.uuid]?.also { documentFile ->
                        if (path.startsWith(volume.path))
                            return findChild(documentFile, path.substring(volume.path.length))
                        if (path.startsWith("/${volume.uuid}"))
                            return findChild(documentFile, path.substring(1 + volume.uuid.length))
                    }
                }
            }
        } else {
            val file = File(path)
            if (file.exists())
                return DocumentFile.fromFile(file)
            else {
                volumeList.forEach {
                    if (path.startsWith("/${it.uuid}")) {
                        val file2 = File("${it.path}${path.substring(1 + it.uuid.length)}")
                        return if (file2.exists())
                            DocumentFile.fromFile(file2)
                        else null
                    }
                }
            }
        }
        return null
    }

    fun reload() {
        volumeList.clear()
        loadGrantUri(context.contentResolver)
        volumeList.addAll(loadVolume(context))
    }

    private fun findChild(documentFile: DocumentFile, path: String): DocumentFile? {
        val names = path.split(File.separatorChar)
        var result: DocumentFile = documentFile
        names.forEach {
            if (it != "")
                result = result.findFile(it) ?: return null
        }
        return result
    }

    /*
   获取全部存储设备信息封装对象
    */

    private fun loadVolume(context: Context): List<Volume> {
        val listStorageVolume = java.util.ArrayList<Volume>()
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        try {
            val methodVolumeList = StorageManager::class.java.getMethod("getVolumeList")
            methodVolumeList.isAccessible = true

            val volumeList = methodVolumeList.invoke(storageManager) as Array<*>
            volumeList.forEach {
                it?.also {
                    try {
                        val path = it.javaClass.getMethod("getPath").invoke(it) as String
                        val isRemovable = it.javaClass.getMethod("isRemovable").invoke(it) as Boolean
                        val state = it.javaClass.getMethod("getState").invoke(it) as String
                        val uuid = (it.javaClass.getMethod("getUuid").invoke(it)
                                ?: if (path == Environment.getExternalStorageDirectory().path) "sdcard" else path.substring(path.lastIndexOf('/'))) as String
                        listStorageVolume.add(Volume(path, isRemovable, state, uuid, grantUris.containsKey(uuid)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        return listStorageVolume
    }

    fun getVolumes(): List<Volume> = volumeList

    fun getAvailableVolumes(): List<Volume> = volumeList.filter { it.state == Environment.MEDIA_MOUNTED }

    /*
     存储设备信息封装类
     */
    data class Volume(val path: String, val isRemovable: Boolean, val state: String, val uuid: String, val isGrant: Boolean)
}