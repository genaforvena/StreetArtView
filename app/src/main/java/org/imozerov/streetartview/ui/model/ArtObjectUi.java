package org.imozerov.streetartview.ui.model;

import org.imozerov.streetartview.storage.model.RealmArtObject;
import org.imozerov.streetartview.storage.model.RealmAuthor;
import org.imozerov.streetartview.storage.model.RealmString;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imozerov on 05.02.16.
 */
public class ArtObjectUi {
    public final String id;
    public final String name;
    public final AuthorUi author;
    public final String description;
    public final String thumbPicUrl;
    public final List<String> picsUrls;

    public ArtObjectUi(String aId,
                       String aName,
                       AuthorUi aAuthor,
                       String aDescription,
                       String aThumbPicUrl,
                       List<String> aPicsUrls) {
        id = aId;
        name = aName;
        author = aAuthor;
        description = aDescription;
        thumbPicUrl = aThumbPicUrl;
        picsUrls = aPicsUrls;
    }

    public ArtObjectUi(RealmArtObject realmArtObject) {
        RealmAuthor realmAuthor = realmArtObject.getAuthor();

        List<String> picUrls = new ArrayList<>();
        for (RealmString realmString : realmArtObject.getPicsUrls()) {
            picUrls.add(realmString.getValue());
        }

        this.id = realmArtObject.getId();
        this.name = realmArtObject.getName();
        this.author =  new AuthorUi(realmAuthor);
        this.description = realmArtObject.getDescription();
        this.thumbPicUrl = realmArtObject.getThumbPicUrl();
        this.picsUrls = picUrls;
    }
}
