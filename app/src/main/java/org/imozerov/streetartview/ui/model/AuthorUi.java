package org.imozerov.streetartview.ui.model;

import org.imozerov.streetartview.storage.model.RealmAuthor;

/**
 * Created by imozerov on 05.02.16.
 */
public class AuthorUi {
    public final String id;
    public final String name;
    public final String description;

    public AuthorUi(String aId, String aName, String aDescription) {
        id = aId;
        name = aName;
        description = aDescription;
    }

    public AuthorUi(RealmAuthor realmAuthor) {
        this.id = realmAuthor.getId();
        this.name = realmAuthor.getName();
        this.description = realmAuthor.getDescription();
    }
}
