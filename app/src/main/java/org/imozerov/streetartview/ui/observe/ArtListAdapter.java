package org.imozerov.streetartview.ui.observe;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.imozerov.streetartview.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by imozerov on 05.02.16.
 */
public class ArtListAdapter extends RecyclerView.Adapter {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.art_object_in_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(rootView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
