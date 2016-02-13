package org.imozerov.streetartview;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by imozerov on 05.02.16.
 */
public class StreetArtViewApp extends Application {
    private AppComponent storageComponent;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("art.realm")
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .migration((realm, oldVersion, newVersion) -> {
                    //TODO implemented migration strategy.
                    })
                .build();
        Realm.setDefaultConfiguration(config);

        storageComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule())
                .build();

        refWatcher = LeakCanary.install(this);
    }

    public AppComponent getStorageComponent() {
        return storageComponent;
    }

    public static RefWatcher getRefWatcher(Context context) {
        StreetArtViewApp application = (StreetArtViewApp) context.getApplicationContext();
        return application.refWatcher;
    }
}
