package org.imozerov.streetartview.ui.explore.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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
        // TODO Ask picasso to fill the holder.thumb
        holder.artObjectName.text = artObject.name
        holder.author.text = artObject.author.name
        holder.artObjectId = artObject.id
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    fun setData(artObjectUis: List<ArtObjectUi>) {
        this.data = artObjectUis
        notifyDataSetChanged()
    }
}
