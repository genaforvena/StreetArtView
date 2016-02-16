package org.imozerov.streetartview.network.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.imozerov.streetartview.network.model.Artwork;
import org.imozerov.streetartview.network.model.Meta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imozerov on 16.02.16.
 */
public class ArtworkListResponse {
    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("artworks")
    @Expose
    private List<Artwork> artworks = new ArrayList<Artwork>();

    /**
     *
     * @return
     * The meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     *
     * @param meta
     * The meta
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     *
     * @return
     * The artworks
     */
    public List<Artwork> getArtworks() {
        return artworks;
    }

    /**
     *
     * @param artworks
     * The artworks
     */
    public void setArtworks(List<Artwork> artworks) {
        this.artworks = artworks;
    }
}
