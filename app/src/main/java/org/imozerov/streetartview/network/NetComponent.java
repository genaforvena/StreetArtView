package org.imozerov.streetartview.network;

import org.imozerov.streetartview.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by imozerov on 16.02.16.
 */
@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(FetchService fetchService);
}
