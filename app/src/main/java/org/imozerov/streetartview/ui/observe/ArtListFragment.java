package org.imozerov.streetartview.ui.observe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.imozerov.streetartview.R;

public class ArtListFragment extends Fragment {
    public ArtListFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_art_list, container, false);
    }
}
