package org.imozerov.streetartview.ui.explore.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.art_object_in_list.view.*
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.model.ArtObjectUi

/**
 * Created by sergei on 09.02.16.
 */
class ArtListViewHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var thumb: ImageView

    internal var artObject: ArtObjectUi? = null

    init {
        val detailOpener: ArtObjectDetailOpener
        try {
            detailOpener = context as ArtObjectDetailOpener
        } catch (cce: ClassCastException) {
            throw RuntimeException("$context must implement ArtObjectDetailOpener interface")
        }

        thumb = itemView.art_object_in_list_image
        itemView.setOnClickListener { v -> detailOpener.openArtObjectDetails(artObject!!) }
    }
}
