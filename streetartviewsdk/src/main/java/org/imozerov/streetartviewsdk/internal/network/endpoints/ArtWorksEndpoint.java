package org.imozerov.streetartviewsdk.internal.network.endpoints;


import org.imozerov.streetartviewsdk.internal.network.model.Artwork;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by imozerov on 16.02.16.
 */
public interface ArtWorksEndpoint {
    @GET("artworks/")
    Call<List<Artwork>> list();
}
