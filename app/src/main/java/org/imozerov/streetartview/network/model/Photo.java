package org.imozerov.streetartview.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by imozerov on 16.02.16.
 */
public class Photo {
    @SerializedName("name")
    @Expose
    private String title;
    @SerializedName("image")
    @Expose
    private String big;

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The big
     */
    public String getBig() {
        return big;
    }

    /**
     *
     * @param big
     * The big
     */
    public void setBig(String big) {
        this.big = big;
    }

}
