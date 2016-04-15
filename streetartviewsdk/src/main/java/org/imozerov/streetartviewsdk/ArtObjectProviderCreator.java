package org.imozerov.streetartviewsdk;

import android.app.Application;

import org.imozerov.streetartviewsdk.internal.DaggerDataSourceComponent;
import org.imozerov.streetartviewsdk.internal.DataSourceComponent;
import org.imozerov.streetartviewsdk.internal.DataSourceModule;
import org.imozerov.streetartviewsdk.internal.network.DaggerNetComponent;
import org.imozerov.streetartviewsdk.internal.network.NetComponent;
import org.imozerov.streetartviewsdk.internal.network.NetModule;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by imozerov on 15.04.16.
 */
public class ArtObjectProviderCreator {
    private static final String PRODUCTION_API = "https://street-art-server.herokuapp.com";

    private DataSourceComponent dataSourceComponent;
    private NetComponent netComponent;

    private static ArtObjectProviderCreator instance;

    public static ArtObjectsProvider createArtObjectsProvider(Application application) {
        instance = new ArtObjectProviderCreator(application);

        return new ArtObjectsProvider(instance);
    }

    private ArtObjectProviderCreator(Application application) {
        RealmConfiguration config = new RealmConfiguration.Builder(application)
                .name("art.realm")
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        //TODO implemented migration strategy.
                    }
                })
                .build();
        Realm.setDefaultConfiguration(config);

        dataSourceComponent = DaggerDataSourceComponent
                .builder()
                .dataSourceModule(new DataSourceModule(application))
                .build();

        netComponent = DaggerNetComponent
                .builder()
                .dataSourceModule(new DataSourceModule(application))
                .netModule(new NetModule(PRODUCTION_API))
                .build();

    }

    public static ArtObjectProviderCreator getInstance() {
        return instance;
    }

    public DataSourceComponent getDataSourceComponent() {
        return dataSourceComponent;
    }

    public NetComponent getNetComponent() {
        return netComponent;
    }
}
