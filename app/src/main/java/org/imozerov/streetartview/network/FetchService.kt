package org.imozerov.streetartview.network

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.DataSource
import org.imozerov.streetartview.storage.model.RealmArtObject
import org.imozerov.streetartview.storage.model.copyDataFromJson
import javax.inject.Inject

class FetchService : IntentService("FetchService") {
    val TAG = "FetchService"

    @Inject
    lateinit var restClient: RestClient
    @Inject
    lateinit var dataSource: DataSource

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

        for (artWork in responseJson) {
            dataSource.insert(artWork)
        }
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
