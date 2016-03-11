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
    protected void initPicassoWithCache() {
        // Test app has to have no Picasso singleton as tests are failed
        // java.lang.IllegalStateException: Singleton instance already exists.
    }

    @Override
    public AppComponent getAppComponent() {
        return appComponent;
    }
}
