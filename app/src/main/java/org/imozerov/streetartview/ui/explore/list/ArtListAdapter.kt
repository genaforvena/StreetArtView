package org.imozerov.streetartview.ui.explore.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import org.imozerov.streetartview.R
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
        holder.artObjectId = artObject.id
        if (artObject.thumbPicUrl.isNotBlank()) {
            Picasso.with(context).load(artObject.thumbPicUrl).into(holder.thumb)
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    fun setData(artObjectUis: List<ArtObjectUi>) {
        data = artObjectUis
        notifyDataSetChanged()
    }
}
