package org.imozerov.streetartview;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

/**
 * Created by imozerov on 04.03.16.
 */
public class TestStreetArtViewApp extends StreetArtViewApp implements TestLifecycleApplication {
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

    @Override
    public void beforeTest(Method method) {

    }

    @Override
    public void prepareTest(Object test) {

    }

    @Override
    public void afterTest(Method method) {

    }
}
