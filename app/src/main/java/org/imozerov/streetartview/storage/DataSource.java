package org.imozerov.streetartview.storage;

import android.os.SystemClock;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

import org.imozerov.streetartview.storage.model.RealmArtObject;
import org.imozerov.streetartview.storage.model.RealmAuthor;
import org.imozerov.streetartview.ui.model.ArtObjectUi;

import rx.Observable;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imozerov on 05.02.16.
 */
public class DataSource {
    Realm realm;

    public DataSource(Realm realm) {
        this.realm = realm;
    }

    public Observable<List<ArtObjectUi>> listArtObjects() {
        return realm.allObjects(RealmArtObject.class)
                .asObservable()
                .map((realmModels) -> {
                    List<ArtObjectUi> listOfArtObjects = new ArrayList<>(realmModels.size());
                    for (RealmArtObject model : realmModels) {
                        listOfArtObjects.add(new ArtObjectUi(model));
                    }
                    return listOfArtObjects;
                });
    }

    public Observable<ArtObjectUi> getArtObject(String id) {
        return realm.where(RealmArtObject.class)
                .equalTo("id", id)
                .findFirst()
                .asObservable()
                .map(t -> new ArtObjectUi((RealmArtObject) t));
    }

    public void addArtObjectStub() {
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
    }
}
