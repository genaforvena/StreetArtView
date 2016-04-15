package org.imozerov.streetartviewsdk.internal;

import android.app.Application;

import org.imozerov.streetartview.storage.DataSource;
import org.imozerov.streetartview.storage.IDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by imozerov on 15.04.16.
 */
@Module
public class DataSourceModule {
    Application application;

    public DataSourceModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    public IDataSource provideDataSource() {
        return new DataSource();
    }
}
