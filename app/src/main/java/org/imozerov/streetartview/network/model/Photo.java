package org.imozerov.streetartview.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by imozerov on 16.02.16.
 */
public class Photo {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("thumbnail")
    @Expose
    private String thmb;
    @SerializedName("fullres")
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
     * The thmb
     */
    public String getThmb() {
        return thmb;
    }

    /**
     *
     * @param thmb
     * The thmb
     */
    public void setThmb(String thmb) {
        this.thmb = thmb;
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
