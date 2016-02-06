package org.imozerov.streetartview.ui.catalog;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.storage.DataSource;
import org.imozerov.streetartview.storage.model.RealmArtObject;
import org.imozerov.streetartview.storage.model.RealmAuthor;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
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

        // TODO Remove this button from here and from xml. It is needed just for testing.
        rootView.findViewById(R.id.button).setOnClickListener((v) -> {
            Log.d(TAG, "adding new ");
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmAuthor realmAuthor = new RealmAuthor();
            realmAuthor.setId("1");
            realmAuthor.setName("Vasya");
            realmAuthor.setDescription("Description");

            RealmArtObject realmArtObject = new RealmArtObject();
            realmArtObject.setAuthor(realmAuthor);
            realmArtObject.setDescription("Description");
            realmArtObject.setName("Name");
            realmArtObject.setId(String.valueOf(SystemClock.currentThreadTimeMillis()));
            realmArtObject.setThumbPicUrl("Pic");
            realmArtObject.setPicsUrls(new RealmList<>());

            realm.copyToRealmOrUpdate(realmArtObject);
            realm.commitTransaction();
            realm.close();
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
                new DataSource()
                .listArtObjects()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((aArtObjectUiList) -> {
                    adapter.setData(aArtObjectUiList);
                }));
    }
}
