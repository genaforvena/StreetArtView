package org.imozerov.streetartview;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.imozerov.streetartview.network.DaggerNetComponent;
import org.imozerov.streetartview.network.FetchService;
import org.imozerov.streetartview.network.NetComponent;
import org.imozerov.streetartview.network.NetModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by imozerov on 05.02.16.
 */
public class StreetArtViewApp extends Application {
    public static final String TEST_API = "http://test.artspots.ru/api/";
    public static final String PRODUCTION_API = "http://artspots.ru/api/";

    private AppComponent appComponent;
    private NetComponent netComponent;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("art.realm")
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .migration((realm, oldVersion, newVersion) -> {
                    //TODO implemented migration strategy.
                    })
                .build();
        Realm.setDefaultConfiguration(config);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();

        netComponent = DaggerNetComponent
                .builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(TEST_API))
                .build();

        refWatcher = LeakCanary.install(this);

        FetchService.Companion.startFetch(this);
    }

    public NetComponent getNetComponent() {
        return netComponent;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static RefWatcher getRefWatcher(Context context) {
        StreetArtViewApp application = (StreetArtViewApp) context.getApplicationContext();
        return application.refWatcher;
    }
}
