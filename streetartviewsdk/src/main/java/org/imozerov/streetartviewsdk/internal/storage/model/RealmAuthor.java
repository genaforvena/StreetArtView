package org.imozerov.streetartviewsdk.internal.storage.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by imozerov on 05.02.16.
 */
public class RealmAuthor extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String photo;

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String aDescription) {
        photo = aDescription;
    }
}
