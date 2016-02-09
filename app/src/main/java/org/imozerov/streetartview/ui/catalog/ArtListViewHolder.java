
package org.imozerov.streetartview.ui.catalog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.ui.detail.ArtObjectDetailOpener;
import org.imozerov.streetartview.ui.model.ArtObjectUi;

/**
 * Created by sergei on 09.02.16.
 */
public class ArtListViewHolder extends RecyclerView.ViewHolder {
    TextView author;
    TextView artObjectName;
    CircleImageView thumb;

    String artObjectId;

    public ArtListViewHolder(Context aContext, View itemView) {
        super(itemView);

        ArtObjectDetailOpener detailOpener;
        try {
            detailOpener = (ArtObjectDetailOpener) aContext;
        } catch (ClassCastException cce) {
            throw new RuntimeException("Activity must implement ArtObjectDetailOpener interface");
        }

        this.author = (TextView) itemView.findViewById(R.id.art_object_view_in_list_author);
        this.artObjectName = (TextView) itemView.findViewById(R.id.art_object_view_in_list_name);
        this.thumb = (CircleImageView) itemView.findViewById(R.id.art_object_view_in_list_thumb);

        itemView.setOnClickListener(v -> {
            if (this.artObjectId != null) {
                detailOpener.openDetailActivity(this.artObjectId);
            } else {
                throw new RuntimeException("ViewHolder cannot show details because artObjectId is invalid");
            }
        });
    }
}
