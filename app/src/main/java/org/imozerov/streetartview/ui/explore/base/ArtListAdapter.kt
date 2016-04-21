package org.imozerov.streetartview.ui.explore.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.extensions.loadImage
import org.imozerov.streetartview.ui.model.ArtObjectUi

/**
 * Created by imozerov on 05.02.16.
 */
class ArtListAdapter(private val context: Context, private var data: List<ArtObjectUi>?) : RecyclerView.Adapter<ArtListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtListViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.art_object_in_list, parent, false)
        return ArtListViewHolder(this.context, rootView)
    }

    override fun onBindViewHolder(holder: ArtListViewHolder, position: Int) {
        val artObject = data!![position]
        if (holder.artObjectId != artObject.id) {
            holder.thumb.loadImage(artObject.thumbPicUrl)
        }
        holder.artObjectId = artObject.id
    }

    override fun getItemCount() = data!!.size

    override fun getItemId(position: Int) = data!![position].id.hashCode().toLong()

    fun setData(artObjectUis: List<ArtObjectUi>) {
        if (artObjectUis == data) {
            return
        }
        data = artObjectUis
        notifyDataSetChanged()
    }
}
