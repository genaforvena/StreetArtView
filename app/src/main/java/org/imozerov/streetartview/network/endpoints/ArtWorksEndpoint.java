package org.imozerov.streetartview.network.endpoints;

import org.imozerov.streetartview.network.model.Artwork;

import retrofit.Call;
import retrofit.http.GET;

import java.util.List;

/**
 * Created by imozerov on 16.02.16.
 */
public interface ArtWorksEndpoint {
    @GET("artworks/")
    Call<List<Artwork>> list();
}
