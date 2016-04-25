package org.imozerov.streetartview

import android.app.Application

import org.imozerov.streetartview.storage.IDataSource
import org.mockito.Mockito

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class TestAppModule(application: Application) : AppModule(application) {

    @Provides
    @Singleton
    override fun providesApplication(): Application {
        return application
    }

    @Provides
    @Singleton
    override fun provideDataSource(): IDataSource {
        return dataSourceMock
    }

    companion object {
        val dataSourceMock = Mockito.mock(IDataSource::class.java, Mockito.RETURNS_MOCKS)
    }
}
