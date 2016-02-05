package org.imozerov.streetartview.ui.observe;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.storage.DataSource;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ArtListFragment extends Fragment {
    private static final String TAG = "ArtListFragment";

    private RecyclerView listView;
    private CompositeSubscription compositeSubscription;
    private ArtListAdapter adapter;


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
        Context context = getContext();

        listView = (RecyclerView) rootView.findViewById(R.id.art_objects_recycler_view);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new ArtListAdapter(new ArrayList<>());
        listView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        compositeSubscription = new CompositeSubscription();
        compositeSubscription.add(
                new DataSource()
                .listArtObjects()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((aArtObjectUiList) -> {
                    adapter.setData(aArtObjectUiList);
                }));
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeSubscription.unsubscribe();
    }
}
