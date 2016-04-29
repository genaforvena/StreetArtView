package org.imozerov.streetartview

import org.imozerov.streetartview.network.NetModule

/**
 * Created by imozerov on 04.03.16.
 */
class TestStreetArtViewApp : StreetArtViewApp() {
    override var appComponent: AppComponent? = null
    private val mockUrl = "https://dummy.com"

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(TestAppModule(this))
                .netModule(NetModule(mockUrl))
                .build()
    }
}
