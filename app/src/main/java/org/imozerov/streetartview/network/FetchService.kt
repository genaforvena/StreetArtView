package org.imozerov.streetartview.network

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.network.internal.RestClient
import org.imozerov.streetartview.storage.DataSource
import javax.inject.Inject

class FetchService : IntentService("FetchService") {
    val TAG = "FetchService"

    @Inject
    lateinit var restClient: RestClient

    @Inject
    lateinit var dataSource: DataSource

    override fun onCreate() {
        super.onCreate()
        (application as StreetArtViewApp).netComponent.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent($intent)")
        if (intent != null) {
            val action = intent.action
            if (ACTION_FETCH_ART_OBJECTS == action) {
                handleStartFetchAction()
            }
        }
    }

    private fun handleStartFetchAction() {
        try {
            val response = restClient.artWorksEndpoint.list().execute()

            if (!response.isSuccess) {
                Log.e(TAG, "Unable to fetch data from server! ${response.code()}")
                return
            }

            val responseJson = response.body().artworks

            dataSource.insert(responseJson)
        } catch (exception: java.io.IOException) {
            Log.w(TAG, "Unable to sync art objects with server", exception)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(EVENT_FETCH_FINISHED))
    }

    companion object {
        val EVENT_FETCH_FINISHED = "org.imozerov.streetartview.network.action.FETCH_FINISHED"
        private val ACTION_FETCH_ART_OBJECTS = "org.imozerov.streetartview.network.action.FETCH_ART_OBJECTS"

        fun startFetch(context: Context) {
            val intent = Intent(context, FetchService::class.java)
            intent.action = ACTION_FETCH_ART_OBJECTS
            context.startService(intent)
        }
    }
}
