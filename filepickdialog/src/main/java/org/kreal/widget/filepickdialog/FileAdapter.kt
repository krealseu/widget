package org.kreal.widget.filepickdialog

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.io.File

/**
 * Created by lthee on 2018/1/6.
 */
class FileAdapter(private val fileSource: FileSource, clickListener: OnItemClickListener? = null) : RecyclerView.Adapter<FileAdapter.ItemView>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    var clickListener: OnItemClickListener? = clickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        val itemView = ItemView(view)
        view.setOnClickListener {
            clickListener?.onItemClick(itemView.adapterPosition)
        }
//        view.setOnLongClickListener {
//            Log.i("dfasdfas", "k$itemView.adapterPosition")
//            true
//        }
        return itemView
    }

    override fun onBindViewHolder(holder: ItemView?, position: Int) {
        holder?.bindDate(fileSource.getIndex(position), fileSource.getIndexState(position))
    }

    override fun getItemCount() = fileSource.size

    class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.fileName)
        private val image: ImageView = itemView.findViewById(R.id.fileImage)
        private val shadow: View = itemView.findViewById(R.id.item_shadow)

        enum class FileType {
            Folder, File, Music, Video, Image
        }

        fun bindDate(file: File, select: Boolean = false) {
            val name = file.name
            text.text = name
            val type: FileType = if (file.isDirectory) FileType.Folder
            else if (name.endsWith(".jpg", true))
                FileType.Image
            else if (name.endsWith(".mp4", true))
                FileType.Video
            else if (name.endsWith(".mp3", true))
                FileType.Music
            else
                FileType.File
            image.setImageResource(when (type) {
                FileType.Folder -> R.drawable.folder
                FileType.File -> R.drawable.file
                FileType.Music -> R.drawable.file_audio
                FileType.Video -> R.drawable.file_video
                FileType.Image -> R.drawable.file_image
            })
            shadow.visibility = if (select) View.VISIBLE else View.INVISIBLE
        }
    }
}