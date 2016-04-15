package org.imozerov.streetartviewsdk.internal.storage.model;

import io.realm.RealmObject;

/**
 * Created by imozerov on 05.02.16.
 */
public class RealmString extends RealmObject {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String aValue) {
        value = aValue;
    }
}
