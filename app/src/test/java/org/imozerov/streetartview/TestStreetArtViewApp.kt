package org.imozerov.streetartview

/**
 * Created by imozerov on 04.03.16.
 */
class TestStreetArtViewApp : StreetArtViewApp() {
    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().appModule(TestAppModule(this)).build()
    }

    override fun getAppComponent(): AppComponent {
        return appComponent
    }
}
