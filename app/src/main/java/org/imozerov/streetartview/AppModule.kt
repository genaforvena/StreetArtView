package org.imozerov.streetartview

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import dagger.Module
import dagger.Provides
import org.imozerov.streetartview.bus.RxBus
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

    @Provides
    @Singleton
    fun provideBus() : RxBus {
        return RxBus()
    }

    @Provides
    @Singleton
    fun provideTracker(application: Application): Tracker {
        val analytics = GoogleAnalytics.getInstance(application)
        return analytics.newTracker("UA-76576531-1")
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }
}
