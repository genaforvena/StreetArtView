package org.imozerov.streetartview.network.endpoints;

import org.imozerov.streetartview.network.json.ArtworkListResponse;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by imozerov on 16.02.16.
 */
public interface ArtWorksEndpoint {
    @GET("artworks/")
    Call<ArtworkListResponse> list();
}
