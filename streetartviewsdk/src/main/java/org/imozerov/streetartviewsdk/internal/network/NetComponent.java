package org.imozerov.streetartviewsdk.internal.network;

import org.imozerov.streetartviewsdk.internal.DataSourceModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by imozerov on 16.02.16.
 */
@Singleton
@Component(modules = {DataSourceModule.class, NetModule.class})
public interface NetComponent {
    void inject(FetchService fetchService);
}
