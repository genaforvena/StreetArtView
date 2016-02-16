package org.imozerov.streetartview.network

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import io.realm.Realm
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.DataSource
import javax.inject.Inject

class FetchService : IntentService("FetchService") {
    val TAG = "FetchService"

    @Inject
    lateinit var restClient: RestClient

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_FETCH_ART_OBJECTS == action) {
                // We have to inject objects here as we cannot pass realm instance between threads
                // so this initialization cannot be done in onCreate callback.
                // Although practically it will work in the same way (onCreate is called on each call for IntentService)
                (application as StreetArtViewApp).netComponent.inject(this)
                handleStartFetchAction()
            }
        }
    }

    private fun handleStartFetchAction() {
        val response = restClient.artWorksEndpoint.list().execute()
        if (!response.isSuccess) {
            Log.e(TAG, "Unable to fetch data from server! ${response.code()}")
            return
        }

        val responseJson = response.body().artworks

        // TODO add injection of the data source!
        // For now it cannot be done as it can lead to access to the realm instance
        // from the wrong thread.
        val dataSource = DataSource(Realm.getDefaultInstance())
        dataSource.insert(responseJson)
    }

    companion object {
        private val ACTION_FETCH_ART_OBJECTS = "org.imozerov.streetartview.network.action.FETCH_ART_OBJECTS"

        fun startFetch(context: Context) {
            val intent = Intent(context, FetchService::class.java)
            intent.action = ACTION_FETCH_ART_OBJECTS
            context.startService(intent)
        }
    }
}
