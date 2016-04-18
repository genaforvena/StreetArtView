package org.imozerov.streetartview;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

import org.imozerov.streetartview.network.DaggerNetComponent;
import org.imozerov.streetartview.network.FetchService;
import org.imozerov.streetartview.network.NetComponent;
import org.imozerov.streetartview.network.NetModule;

/**
 * Created by imozerov on 05.02.16.
 */
public class StreetArtViewApp extends Application {
    public static final String PRODUCTION_API = "https://street-art-server.herokuapp.com";

    private AppComponent appComponent;
    private NetComponent netComponent;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("art.realm")
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(2)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        // TODO
                    }
                })
                .build();
        Realm.setDefaultConfiguration(config);

        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();

        netComponent = DaggerNetComponent
                .builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(PRODUCTION_API))
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
