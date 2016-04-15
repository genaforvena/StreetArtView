package org.imozerov.streetartviewsdk.internal.storage.model;

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
    private RealmList<RealmAuthor> authors;
    private String description;
    private String thumbPicUrl;
    private RealmLocation location;
    private RealmList<RealmString> picsUrls;
    private boolean isFavourite;
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<RealmAuthor> getAuthors() {
        return authors;
    }

    public void setAuthors(RealmList<RealmAuthor> authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbPicUrl() {
        return thumbPicUrl;
    }

    public void setThumbPicUrl(String thumbPicUrl) {
        this.thumbPicUrl = thumbPicUrl;
    }

    public RealmLocation getLocation() {
        return location;
    }

    public void setLocation(RealmLocation location) {
        this.location = location;
    }

    public RealmList<RealmString> getPicsUrls() {
        return picsUrls;
    }

    public void setPicsUrls(RealmList<RealmString> picsUrls) {
        this.picsUrls = picsUrls;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
