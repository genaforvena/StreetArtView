package org.imozerov.streetartviewsdk.internal;

import org.imozerov.streetartviewsdk.ArtObjectsProvider;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by imozerov on 15.04.16.
 */
@Singleton
@Component(modules = DataSourceModule.class)
public interface DataSourceComponent {
    void inject(ArtObjectsProvider artObjectsProvider);
}
