package org.imozerov.streetartview.storage.model;

import org.imozerov.streetartview.ui.model.AuthorUi;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by imozerov on 05.02.16.
 */
public class ArtObjectModel extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private AuthorModel author;
    private String description;
    private String thumbPicUrl;
    private RealmList<RealmString> picsUrls;

    public String getId() {
        return id;
    }

    public void setId(String aId) {
        id = aId;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public AuthorModel getAuthor() {
        return author;
    }

    public void setAuthor(AuthorModel aAuthor) {
        author = aAuthor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public String getThumbPicUrl() {
        return thumbPicUrl;
    }

    public void setThumbPicUrl(String aThumbPicUrl) {
        thumbPicUrl = aThumbPicUrl;
    }

    public RealmList<RealmString> getPicsUrls() {
        return picsUrls;
    }

    public void setPicsUrls(RealmList<RealmString> aPicsUrls) {
        picsUrls = aPicsUrls;
    }
}
