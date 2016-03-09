package org.imozerov.streetartview;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

import org.imozerov.streetartview.storage.DataSource;
import org.imozerov.streetartview.storage.IDataSource;
import org.mockito.Mockito;

import javax.inject.Singleton;

/**
 * Created by imozerov on 06.02.16.
 */
@Module
public class TestAppModule extends AppModule {
    public static final IDataSource dataSourceMock = Mockito.mock(IDataSource.class, Mockito.RETURNS_MOCKS);

    public TestAppModule(Application application) {
        super(application);
    }

    @Provides
    @Singleton
    @Override
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    @Override
    public IDataSource provideDataSource() {
        return dataSourceMock;
    }
}
