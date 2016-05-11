package org.imozerov.streetartview.ui.add

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.art_object_in_list.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.custom.CursorRecyclerViewAdapter

/**
 * Created by imozerov on 11.05.16.
 */
class SdCardImagesAdapter(private val context: Context, cursor: Cursor) : CursorRecyclerViewAdapter<SdCardImagesAdapter.ViewHolder>(context, cursor) {
    val imageIdCursorIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID)

    class ViewHolder(context: Context, view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView
        var imageId: Int? = null

        init {
            val imagePickListener: OnImagePickListener
            try {
                imagePickListener = context as OnImagePickListener
            } catch (cce: ClassCastException) {
                throw RuntimeException("$context must implement ArtObjectDetailOpener interface")
            }

            imageView = view.art_object_in_list_image
            imageView.setOnClickListener { imagePickListener.onImagePicked(imageId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.art_object_in_list, parent, false)
        val vh = ViewHolder(context, itemView)
        return vh
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, cursor: Cursor) {
        val imageId = cursor.getInt(imageIdCursorIndex)
        val uri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, imageId.toString())
        viewHolder.imageView.setImageURI(uri)
        viewHolder.imageId = imageId
    }
}