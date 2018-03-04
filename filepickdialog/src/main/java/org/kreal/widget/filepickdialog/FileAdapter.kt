package org.kreal.widget.filepickdialog

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by lthee on 2018/1/6.
 * 适配器，将数据的数据模型绑定到view上
 */
class FileAdapter(private val fileSource: FileSource, val clickListener: OnItemClickListener? = null) : RecyclerView.Adapter<FileAdapter.ItemView>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

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
        holder?.apply {
            val data = fileSource.getIndex(position)
            text.text = data.name
            if (data.isDirectory) {
                image.setImageResource(R.drawable.folder)
            } else if (data.isFile) {
                val type = data.type.substring(0, 5)
                image.setImageResource(when (type) {
                    "video" -> R.drawable.file_video
                    "image" -> R.drawable.file_image
                    "audio" -> R.drawable.file_audio
                    else -> R.drawable.file
                })
            }
            shadow.visibility = if (fileSource.getState(data) != 0) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun getItemCount() = fileSource.size

    class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.fileName)
        val image: ImageView = itemView.findViewById(R.id.fileImage)
        val shadow: View = itemView.findViewById(R.id.item_shadow)
    }
}