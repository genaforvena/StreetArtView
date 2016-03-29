package org.imozerov.streetartview

import android.app.Application

import dagger.Module
import dagger.Provides

import org.imozerov.streetartview.storage.DataSource
import org.imozerov.streetartview.storage.IDataSource

import javax.inject.Singleton

@Module
open class AppModule(var application: Application) {

    @Provides
    @Singleton
    internal open fun providesApplication(): Application {
        return application
    }

    @Provides
    @Singleton
    open fun provideDataSource(): IDataSource {
        return DataSource()
    }
}
