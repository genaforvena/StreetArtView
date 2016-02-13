package org.imozerov.streetartview.ui.explore.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_art_object_in_list.view.*
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener

/**
 * Created by sergei on 09.02.16.
 */
class ArtListViewHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var author: TextView
    internal var artObjectName: TextView
    internal var thumb: CircleImageView

    internal var artObjectId: String? = null

    init {
        val detailOpener: ArtObjectDetailOpener
        try {
            detailOpener = context as ArtObjectDetailOpener
        } catch (cce: ClassCastException) {
            throw RuntimeException("ExploreArtActivity must implement ArtObjectDetailOpener interface")
        }

        author = itemView.art_object_view_in_list_author
        artObjectName = itemView.art_object_view_in_list_name
        thumb = itemView.art_object_view_in_list_thumb

        itemView.setOnClickListener { v -> detailOpener.openArtObjectDetails(artObjectId) }
    }
}
