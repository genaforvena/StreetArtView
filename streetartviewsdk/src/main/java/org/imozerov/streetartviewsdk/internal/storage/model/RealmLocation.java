package org.imozerov.streetartviewsdk.internal.storage.model;

import io.realm.RealmObject;

/**
 * Created by imozerov on 26.02.16.
 */
public class RealmLocation extends RealmObject {
    private String address;
    private double lat;
    private double lng;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
