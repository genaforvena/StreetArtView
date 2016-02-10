package org.imozerov.streetartview.ui.catalog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.ui.model.ArtObjectUi;

import java.util.List;

/**
 * Created by imozerov on 05.02.16.
 */
public class ArtListAdapter extends RecyclerView.Adapter<ArtListViewHolder> {

    private Context context;
    private List<ArtObjectUi> data;

    public ArtListAdapter(Context context, @NonNull List<ArtObjectUi> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ArtListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.art_object_in_list, parent, false);
        return new ArtListViewHolder(this.context, rootView);
    }

    @Override
    public void onBindViewHolder(ArtListViewHolder holder, int position) {
        ArtObjectUi artObject = data.get(position);
        // TODO Ask picasso to fill the holder.thumb
        holder.artObjectName.setText(artObject.name);
        holder.author.setText(artObject.author.name);
        holder.artObjectId = artObject.id;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<ArtObjectUi> artObjectUis) {
        this.data = artObjectUis;
        notifyDataSetChanged();
    }
}
