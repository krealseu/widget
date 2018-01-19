package org.kreal.widget.filepickdialog

/**
 * Created by lthee on 2018/1/19.
 * data model
 */
data class FileModel(val name: String, val type: FileType = FileType.File, val select: Boolean = false) {
    enum class FileType {
        Folder, File, Music, Video, Image
    }
}