package org.imozerov.streetartview.network

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.bus.RxBus
import org.imozerov.streetartview.bus.events.FetchFinishedEvent
import org.imozerov.streetartview.network.internal.RestClient
import org.imozerov.streetartview.storage.IDataSource
import javax.inject.Inject

class FetchService : IntentService("FetchService") {
    val TAG = "FetchService"

    @Inject
    lateinit var rxBus: RxBus

    @Inject
    lateinit var restClient: RestClient

    @Inject
    lateinit var dataSource: IDataSource

    override fun onCreate() {
        super.onCreate()
        (application as StreetArtViewApp).appComponent!!.inject(this)
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
        var isSuccess = false
        try {
            val response = restClient.artWorksEndpoint.list().execute()

            isSuccess = response.isSuccess
            if (isSuccess) {
                dataSource.insert(response.body())
            } else {
                Log.e(TAG, "Unable to fetch data from server! ${response.code()}")
            }
        } catch (exception: java.io.IOException) {
            Log.w(TAG, "Unable to sync art objects with server", exception)
        }

        rxBus.post(FetchFinishedEvent(isSuccess))
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
