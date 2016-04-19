package org.imozerov.streetartview;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import dagger.Module;
import dagger.Provides;

import org.imozerov.streetartview.storage.DataSource;
import org.imozerov.streetartview.storage.IDataSource;

import javax.inject.Singleton;

/**
 * Created by imozerov on 06.02.16.
 */
@Module
public class AppModule {
    Application application;

    public AppModule(Application application) {
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

    @Provides
    @Singleton
    public Tracker provideTracker(Application application) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(application);
        return analytics.newTracker("UA-76576531-1");
    }
}
