package org.kreal.widget.filepickdialog

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
            image.setImageResource(when (data.type) {
                FileModel.FileType.Folder -> R.drawable.folder
                FileModel.FileType.File -> R.drawable.file
                FileModel.FileType.Music -> R.drawable.file_audio
                FileModel.FileType.Video -> R.drawable.file_video
                FileModel.FileType.Image -> R.drawable.file_image
            })
            shadow.visibility = if (data.select) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun getItemCount() = fileSource.size

    class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.fileName)
        val image: ImageView = itemView.findViewById(R.id.fileImage)
        val shadow: View = itemView.findViewById(R.id.item_shadow)
    }
}