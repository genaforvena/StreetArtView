package org.imozerov.streetartview.ui.observe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.imozerov.streetartview.R;

public class ArtListFragment extends Fragment {
    private RecyclerView listView;

    public ArtListFragment() {
    }

    public static ArtListFragment newInstance() {
        ArtListFragment fragment = new ArtListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_art_list, container, false);

        listView = (RecyclerView) rootView.findViewById(R.id.art_objects_recycler_view);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(new ArtListAdapter());

        return rootView;
    }
}
