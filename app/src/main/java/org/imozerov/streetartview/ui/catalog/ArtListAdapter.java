package org.imozerov.streetartview.ui.catalog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.ui.DetailOpener;
import org.imozerov.streetartview.ui.model.ArtObjectUi;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by imozerov on 05.02.16.
 */
public class ArtListAdapter extends RecyclerView.Adapter<ArtListAdapter.ViewHolder> {

    private final Context mContext;
    private List<ArtObjectUi> mData;

    private DetailOpener mDetailOpener;

    public ArtListAdapter(Context context, @NonNull List<ArtObjectUi> data) {
        mContext = context;
        this.mData = data;

        if (context instanceof DetailOpener) {
            mDetailOpener = (DetailOpener) context;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.art_object_in_list, parent, false);
        rootView.setOnClickListener((v) -> {
            if (mDetailOpener != null) {
                mDetailOpener.openDetailActivity();
            }
        });

        ViewHolder viewHolder = new ViewHolder(rootView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArtObjectUi artObject = mData.get(position);
        // TODO Ask picasso to fill the holder.thumb
        holder.artObjectName.setText(artObject.name);
        holder.author.setText(artObject.author.name);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<ArtObjectUi> aArtObjectUis) {
        mData = aArtObjectUis;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView author;
        public TextView artObjectName;
        public CircleImageView thumb;

        public ViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.art_object_view_in_list_author);
            artObjectName = (TextView) itemView.findViewById(R.id.art_object_view_in_list_name);
            thumb = (CircleImageView) itemView.findViewById(R.id.art_object_view_in_list_thumb);
        }
    }
}
