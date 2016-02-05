package org.imozerov.streetartview.storage;

import org.imozerov.streetartview.storage.model.RealmArtObject;
import org.imozerov.streetartview.storage.model.RealmAuthor;
import org.imozerov.streetartview.storage.model.RealmString;
import org.imozerov.streetartview.ui.model.ArtObjectUi;
import org.imozerov.streetartview.ui.model.AuthorUi;

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
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
                .map((realmModels) -> {
                    List<ArtObjectUi> listOfArtObjects = new ArrayList<>(realmModels.size());
                    for (RealmArtObject model : realmModels) {
                        RealmAuthor author = model.getAuthor();

                        AuthorUi authorUi = new AuthorUi(
                                author.getId(),
                                author.getName(),
                                author.getDescription()
                        );

                        List<String> picUrls = new ArrayList<>();
                        for (RealmString realmString : model.getPicsUrls()) {
                            picUrls.add(realmString.getValue());
                        }

                        ArtObjectUi artObjectUi = new ArtObjectUi(
                                model.getId(),
                                model.getName(),
                                authorUi,
                                model.getDescription(),
                                model.getThumbPicUrl(),
                                picUrls
                        );

                        listOfArtObjects.add(artObjectUi);
                    }
                    return listOfArtObjects;
                });
    }
}
