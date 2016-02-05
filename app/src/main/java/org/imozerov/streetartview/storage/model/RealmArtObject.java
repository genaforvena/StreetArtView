package org.imozerov.streetartview.storage.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by imozerov on 05.02.16.
 */
public class RealmArtObject extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private RealmAuthor author;
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

    public RealmAuthor getAuthor() {
        return author;
    }

    public void setAuthor(RealmAuthor aAuthor) {
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
