
package org.imozerov.streetartview.ui.catalog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.ui.detail.ArtObjectDetailOpener;

/**
 * Created by sergei on 09.02.16.
 */
public class ArtListViewHolder extends RecyclerView.ViewHolder {
    TextView author;
    TextView artObjectName;
    CircleImageView thumb;

    String artObjectId;

    public ArtListViewHolder(Context context, View itemView) {
        super(itemView);

        ArtObjectDetailOpener detailOpener;
        try {
            detailOpener = (ArtObjectDetailOpener) context;
        } catch (ClassCastException cce) {
            throw new RuntimeException("ExploreArtActivity must implement ArtObjectDetailOpener interface");
        }

        this.author = (TextView) itemView.findViewById(R.id.art_object_view_in_list_author);
        this.artObjectName = (TextView) itemView.findViewById(R.id.art_object_view_in_list_name);
        this.thumb = (CircleImageView) itemView.findViewById(R.id.art_object_view_in_list_thumb);

        itemView.setOnClickListener(v -> {
            if (this.artObjectId != null) {
                detailOpener.openArtObjectDetails(this.artObjectId);
            } else {
                throw new RuntimeException("ArtListViewHolder cannot show details because artObjectId is invalid");
            }
        });
    }
}
