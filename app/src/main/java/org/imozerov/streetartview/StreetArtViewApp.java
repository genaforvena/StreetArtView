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
                    //TODO
                    })
                .build();
        Realm.setDefaultConfiguration(config);

        // TODO remove this code. It is needed just for now to test realm.
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
        realmArtObject.setId("1");
        realmArtObject.setThumbPicUrl("Pic");
        realmArtObject.setPicsUrls(new RealmList<>());

        realm.copyToRealmOrUpdate(realmArtObject);
        realm.commitTransaction();
        realm.close();

        LeakCanary.install(this);
    }
}
