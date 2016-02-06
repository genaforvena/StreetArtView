package org.imozerov.streetartview.ui.catalog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.StreetArtViewApp;
import org.imozerov.streetartview.storage.DataSource;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ArtListFragment extends Fragment {
    private static final String TAG = "ArtListFragment";

    private ArtListAdapter adapter;
    private RecyclerView listView;

    private CompositeSubscription compositeSubscription;

    @Inject DataSource dataSource;

    public ArtListFragment() {
    }

    public static ArtListFragment newInstance() {
        ArtListFragment fragment = new ArtListFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((StreetArtViewApp) getActivity().getApplication()).getStorageComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_art_list, container, false);
        Context context = getContext();

        listView = (RecyclerView) rootView.findViewById(R.id.art_objects_recycler_view);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new ArtListAdapter(new ArrayList<>());
        listView.setAdapter(adapter);

        // TODO Remove this button from here and from xml. It is needed just for now.
        rootView.findViewById(R.id.button).setOnClickListener((v) -> {
            Log.d(TAG, "adding new ");
            dataSource.addArtObjectStub();
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        compositeSubscription = new CompositeSubscription();
        startFetchingArtObjectsFromDataSource();
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeSubscription.unsubscribe();
    }

    private void startFetchingArtObjectsFromDataSource() {
        compositeSubscription.add(
                dataSource.listArtObjects()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((aArtObjectUiList) -> {
                            adapter.setData(aArtObjectUiList);
                        }));
    }
}
