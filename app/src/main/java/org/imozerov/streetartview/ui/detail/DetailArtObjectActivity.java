
package org.imozerov.streetartview.ui.detail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.StreetArtViewApp;
import org.imozerov.streetartview.storage.DataSource;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;

/**
 * Created by sergei on 08.02.16.
 */
public class DetailArtObjectActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_ART_OBJECT_DETAIL_ID = "EXTRA_KEY_ART_OBJECT_DETAIL_ID";

    private CompositeSubscription compositeSubscription;

    @Inject
    public DataSource dataSource;

    private TextView idTextView;
    private TextView nameTextView;

    private String artObjectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((StreetArtViewApp) getApplication()).getStorageComponent().inject(this);

        setContentView(R.layout.art_object_in_list);

        this.idTextView = (TextView) findViewById(R.id.art_object_view_in_list_author);
        this.nameTextView = (TextView) findViewById(R.id.art_object_view_in_list_name);

        this.artObjectId = getIntent().getStringExtra(EXTRA_KEY_ART_OBJECT_DETAIL_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.compositeSubscription = new CompositeSubscription();
        startFetchingArtObject();
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeSubscription.unsubscribe();
    }

    private void startFetchingArtObject() {
        this.compositeSubscription.add(
                dataSource.getArtObject(this.artObjectId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(t -> {
                            this.idTextView.setText(t.getId());
                            this.nameTextView.setText(t.getName());
                        }));
    }
}
