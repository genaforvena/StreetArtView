package org.imozerov.streetartview;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by imozerov on 05.02.16.
 */
public class StreetArtViewApp extends Application {
    private AppComponent appComponent;
    private RefWatcher refWatcher;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        refWatcher = LeakCanary.install(this);
    }

    public static AppComponent getAppComponent(Context context) {
        StreetArtViewApp application = (StreetArtViewApp) context.getApplicationContext();
        return application.appComponent;
    }

    public static RefWatcher getRefWatcher(Context context) {
        StreetArtViewApp application = (StreetArtViewApp) context.getApplicationContext();
        return application.refWatcher;
    }
}
