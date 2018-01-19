package org.kreal.widget.filepickdialog.util

/**
 * Created by lthee on 2018/1/19.
 * 获取name的MimeType
 */
class MimeHelp {
    private val MIMETYPES = hashMapOf(
            "css" to "text/css",
            "htm" to "text/html",
            "html" to "text/html",
            "xml" to "text/xml",
            "java" to "text/x-java-source, text/java",
            "md" to "text/plain",
            "txt" to "text/plain",
            "asc" to "text/plain",
            "gif" to "image/gif",
            "jpg" to "image/jpeg",
            "jpeg" to "image/jpeg",
            "png" to "image/png",
            "svg" to "image/svg+xml",
            "mp3" to "audio/mpeg",
            "m3u" to "audio/mpeg-url",
            "mp4" to "video/mp4",
            "mkv" to "video/mkv",
            "ogv" to "video/ogg",
            "flv" to "video/x-flv",
            "mov" to "video/quicktime",
            "swf" to "application/x-shockwave-flash",
            "js" to "application/javascript",
            "pdf" to "application/pdf",
            "doc" to "application/msword",
            "ogg" to "application/x-ogg",
            "zip" to "application/octet-stream",
            "exe" to "application/octet-stream",
            "class" to "application/octet-stream",
            "m3u8" to "application/vnd.apple.mpegurl",
            "ts" to "video/mp2t"
    )

    fun getMimeType(name: String): String {
        val dot = name.lastIndexOf('.')
        var mini: String? = null
        if (dot >= 0) {
            mini = MIMETYPES[name.substring(dot + 1).toLowerCase()]
        }
        return if (mini == null) "application/octet-stream" else mini
    }

    fun matches(name: String, filter: String = "*/*"): Boolean {
        val params = filter.split('/')
        val types = getMimeType(name).split('/')
        if (params.size != 2 || types.size != 2)
            return false
        var result = 0
        if (params[0] != "*")
            if (params[0] != types[0])
                result++
        if (params[1] != "*")
            if (params[1] != types[1])
                result++
        return result == 0

    }
}