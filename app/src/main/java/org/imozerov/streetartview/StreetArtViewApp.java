package org.imozerov.streetartview;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import org.imozerov.streetartview.storage.model.RealmArtObject;
import org.imozerov.streetartview.storage.model.RealmAuthor;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmMigration;

/**
 * Created by imozerov on 05.02.16.
 */
public class StreetArtViewApp extends Application {
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

        LeakCanary.install(this);
    }
}
