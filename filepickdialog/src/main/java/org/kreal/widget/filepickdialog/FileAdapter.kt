package org.kreal.widget.filepickdialog

import android.support.v4.provider.DocumentFile
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by lthee on 2018/1/6.
 * 适配器，将数据的数据模型绑定到view上
 */
internal class FileAdapter(private val fileSource: FileSource, private val clickListener: OnItemClickListener? = null) : RecyclerView.Adapter<FileAdapter.ItemView>() {

    interface OnItemClickListener {
        fun onItemClick(data: DocumentFile, position: Int)
    }

    override fun getItemCount() = fileSource.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        val itemView = ItemView(view)
        view.setOnClickListener {
            clickListener?.onItemClick(fileSource[itemView.adapterPosition], itemView.adapterPosition)
        }
        return itemView
    }

    override fun onBindViewHolder(holder: ItemView?, position: Int) {
        holder?.apply {
            val data = fileSource[position]
            text.text = data.name
            if (data.isDirectory)
                image.setImageResource(R.drawable.folder)
            else if (data.isFile) image.setImageResource(when (data.type.substring(0, 5)) {
                "video" -> R.drawable.file_video
                "image" -> R.drawable.file_image
                "audio" -> R.drawable.file_audio
                else -> R.drawable.file
            })
            shadow.visibility = if (fileSource.getState(data) != 0) View.VISIBLE else View.INVISIBLE
        }
    }

    class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.fileName)
        val image: ImageView = itemView.findViewById(R.id.fileImage)
        val shadow: View = itemView.findViewById(R.id.item_shadow)
    }
}