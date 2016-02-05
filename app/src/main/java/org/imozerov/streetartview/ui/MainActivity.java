package org.imozerov.streetartview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.ui.observe.ArtListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, ArtListFragment.newInstance())
                .commit();
    }
}
