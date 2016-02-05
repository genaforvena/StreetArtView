package org.imozerov.streetartview;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by imozerov on 05.02.16.
 */
public class StreetArtViewApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);
    }
}
