package org.imozerov.streetartview.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.ui.catalog.ArtListFragment;

public class MainActivity extends AppCompatActivity implements DetailOpener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, ArtListFragment.newInstance())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void openDetailActivity() {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        startActivity(detailIntent);
    }

}
