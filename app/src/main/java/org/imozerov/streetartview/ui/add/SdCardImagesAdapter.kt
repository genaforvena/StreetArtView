package org.imozerov.streetartview.ui.add

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import org.imozerov.streetartview.R
import java.util.*

class SdCardImagesAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mData: List<PictureItem> = ArrayList<PictureItem>()

    val TAG = "SdCardImagesAdapter"

    fun updateDataset(data: List<PictureItem>) {
        Log.i(TAG, "updateDataset")
        mData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.art_object_in_list, parent, false)
        return ViewHolder(mContext, view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val typedHolder = holder as ViewHolder

        val pictureItem = mData[position]

        typedHolder.mItemId = pictureItem.id!!

        Glide.with(mContext).load(Uri.withAppendedPath(PickImageActivity.IMAGES_PATH,
                pictureItem.id.toString()))
                .thumbnail(0.1f)
                .into(typedHolder.mImage)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    internal class ViewHolder(context: Context, view: View) : RecyclerView.ViewHolder(view) {

        var mItemId: Int = 0
        var mImage: ImageView

        init {
            mImage = view.findViewById(R.id.art_object_in_list_image) as ImageView

            val imagePickListener: OnImagePickListener
            try {
                imagePickListener = context as OnImagePickListener
            } catch (cce: ClassCastException) {
                throw RuntimeException("$context must implement OnImagePickListener interface")
            }

            view.setOnClickListener { imagePickListener.onImagePicked(mItemId) }
        }
    }

}

interface OnImagePickListener {
    fun onImagePicked(imageId: Int)
}


