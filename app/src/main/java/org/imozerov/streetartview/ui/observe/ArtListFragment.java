package org.imozerov.streetartview.ui.observe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.ui.model.ArtObjectUi;
import org.imozerov.streetartview.ui.model.AuthorUi;

import java.util.ArrayList;
import java.util.List;

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

        List<ArtObjectUi> artObjectUiStub = new ArrayList<>();
        artObjectUiStub.add(new ArtObjectUi("1", "Name",
                new AuthorUi("1", "Vasya", "Molodez"),
                "Description", "Thumb", new ArrayList<>()));
        artObjectUiStub.add(new ArtObjectUi("2", "Name2",
                new AuthorUi("1", "Vasya", "Molodez"),
                "Description", "Thumb", new ArrayList<>()));
        artObjectUiStub.add(new ArtObjectUi("3", "Name3",
                new AuthorUi("1", "Vasya", "Molodez"),
                "Description", "Thumb", new ArrayList<>()));
        listView.setAdapter(new ArtListAdapter(artObjectUiStub));

        return rootView;
    }
}
