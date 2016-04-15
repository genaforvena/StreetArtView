package org.imozerov.streetartviewsdk.internal.network

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartviewsdk.ArtObjectProviderCreator
import org.imozerov.streetartviewsdk.internal.network.internal.RestClient
import rx.Observable
import java.io.IOException
import javax.inject.Inject

class FetchService : IntentService("FetchService") {
    val TAG = "FetchService"

    @Inject
    lateinit var restClient: RestClient

    @Inject
    lateinit var dataSource: IDataSource

    override fun onCreate() {
        super.onCreate()
        ArtObjectProviderCreator.getInstance().netComponent.inject(this)
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

            val responseJson = response.body()

            dataSource.insert(responseJson)
        } catch (exception: IOException) {
            Log.w(TAG, "Unable to sync art objects with server", exception)
        }
        // TODO
//        BroadcastManager.getInstance(this).sendBroadcast(Intent(EVENT_FETCH_FINISHED))
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
