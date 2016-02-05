package org.imozerov.streetartview.storage;

import org.imozerov.streetartview.ui.model.ArtObjectUi;
import org.imozerov.streetartview.ui.model.AuthorUi;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by imozerov on 05.02.16.
 */
public class DataSource {
    public Observable<List<ArtObjectUi>> listArtObjects() {
        List<ArtObjectUi> artObjectUiStub = new ArrayList<>();
        artObjectUiStub.add(new ArtObjectUi("1", "Name",
                new AuthorUi("1", "Vasya", "Molodez"),
                "Description", "Thumb", new ArrayList<>()));
        artObjectUiStub.add(new ArtObjectUi("2", "Name2",
                new AuthorUi("1", "Vasya", "Molodez"),
                "Description", "Thumb", new ArrayList<>()));
        artObjectUiStub.add(new ArtObjectUi("3", "Name3",
                new AuthorUi("1", "Vasya", "Molodez"),
                "Description", "Thumb", new ArrayList<>()));

        return Observable.just(artObjectUiStub);
    }
}
