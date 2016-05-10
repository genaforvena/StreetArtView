package org.imozerov.streetartview.ui.add

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_add_art_object.*
import kotlinx.android.synthetic.main.art_object_in_list.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.custom.CursorRecyclerViewAdapter

class PickImageActivity : AppCompatActivity(), OnImagePickListener {
    private val adapter by lazy { SdCardImagesAdapter(this, cursor!!) }

    private var cursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_art_object)

        val list = arrayOf(MediaStore.Images.Media._ID)

        cursor = contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, list, null, null, MediaStore.Images.Thumbnails._ID);

        val layoutManager = GridLayoutManager(this, 3)
        adapter.setHasStableIds(true)
        sd_card_images.layoutManager = layoutManager
        sd_card_images.adapter = adapter
    }

    override fun onImagePicked(imageUri: Uri?) {
        image_picked.setImageURI(imageUri)
    }

    class SdCardImagesAdapter(private val context: Context, cursor: Cursor) : CursorRecyclerViewAdapter<SdCardImagesAdapter.ViewHolder>(context, cursor) {
        val imageIdCursorIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID)

        class ViewHolder(context: Context, view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView
            var imageUri: Uri? = null

            init {
                val imagePickListener: OnImagePickListener
                try {
                    imagePickListener = context as OnImagePickListener
                } catch (cce: ClassCastException) {
                    throw RuntimeException("$context must implement ArtObjectDetailOpener interface")
                }

                imageView = view.art_object_in_list_image
                imageView.setOnClickListener { imagePickListener.onImagePicked(imageUri) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.art_object_in_list, parent, false)
            val vh = ViewHolder(context, itemView)
            return vh
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, cursor: Cursor) {
            val imageID = cursor.getInt(imageIdCursorIndex)
            val uri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID)
            viewHolder.imageView.setImageURI(uri)
            viewHolder.imageUri = uri
        }


    }
}

interface OnImagePickListener {
    fun onImagePicked(imageUri: Uri?)
}
