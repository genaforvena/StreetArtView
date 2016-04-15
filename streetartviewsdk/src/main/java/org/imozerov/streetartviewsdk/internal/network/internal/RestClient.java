package org.imozerov.streetartviewsdk.internal.network.internal;

import org.imozerov.streetartviewsdk.internal.network.endpoints.ArtWorksEndpoint;

import retrofit.Retrofit;


/**
 * Created by imozerov on 16.02.16.
 */
public class RestClient {
    ArtWorksEndpoint artWorksEndpoint;

    public RestClient(Retrofit retrofit) {
        artWorksEndpoint = retrofit.create(ArtWorksEndpoint.class);
    }

    public ArtWorksEndpoint getArtWorksEndpoint() {
        return artWorksEndpoint;
    }
}
