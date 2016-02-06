package org.imozerov.streetartview.storage;

import org.imozerov.streetartview.storage.model.RealmArtObject;
import org.imozerov.streetartview.ui.model.ArtObjectUi;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;

/**
 * Created by imozerov on 05.02.16.
 */
public class DataSource {
    public Observable<List<ArtObjectUi>> listArtObjects() {
        Realm realm = Realm.getDefaultInstance();
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
}
