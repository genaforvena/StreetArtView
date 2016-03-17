package org.imozerov.streetartview;

/**
 * Created by imozerov on 04.03.16.
 */
public class TestStreetArtViewApp extends StreetArtViewApp {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new TestAppModule(this))
                .build();
    }

    @Override
    public AppComponent getAppComponent() {
        return appComponent;
    }
}
