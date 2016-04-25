package org.imozerov.streetartview

import android.app.Application
import android.content.Context

import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

import io.realm.Realm
import io.realm.RealmConfiguration

import org.imozerov.streetartview.network.DaggerNetComponent
import org.imozerov.streetartview.network.FetchService
import org.imozerov.streetartview.network.NetComponent
import org.imozerov.streetartview.network.NetModule

open class StreetArtViewApp : Application() {

    open var appComponent: AppComponent? = null
    var netComponent: NetComponent? = null
        private set
    private var refWatcher: RefWatcher? = null

    override fun onCreate() {
        super.onCreate()

        val config = RealmConfiguration.Builder(this).name("art.realm").deleteRealmIfMigrationNeeded().schemaVersion(2).migration { realm, oldVersion, newVersion -> }.build()
        Realm.setDefaultConfiguration(config)

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()

        netComponent = DaggerNetComponent.builder().appModule(AppModule(this)).netModule(NetModule(PRODUCTION_API)).build()

        refWatcher = LeakCanary.install(this)

        FetchService.startFetch(this)
    }

    companion object {
        val PRODUCTION_API = "https://street-art-server.herokuapp.com"

        fun getRefWatcher(context: Context): RefWatcher {
            val application = context.applicationContext as StreetArtViewApp
            return application.refWatcher!!
        }
    }
}
