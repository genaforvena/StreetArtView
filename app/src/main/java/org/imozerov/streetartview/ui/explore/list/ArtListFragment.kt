package org.imozerov.streetartview.ui.explore.list

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_art_list.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.network.FetchService
import org.imozerov.streetartview.ui.explore.ArtListPresenter
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.model.ArtObjectUi

class ArtListFragment : Fragment(), Filterable, ArtView {
    private val TAG = "ArtListFragment"

    private var presenter: ArtListPresenter? = null
    private var adapter: ArtListAdapter? = null

    private var fetchFinishedBroadcastReceiver: BroadcastReceiver? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_art_list, container, false)

        presenter = ArtListPresenter(this)

        adapter = ArtListAdapter(context, java.util.ArrayList<ArtObjectUi>())
        rootView.art_objects_recycler_view.setHasFixedSize(true)

        val layoutManager = GridLayoutManager(context, 3)
        rootView.art_objects_recycler_view.layoutManager = layoutManager
        rootView.art_objects_recycler_view.adapter = adapter
        rootView.swipe_to_refresh_layout.setOnRefreshListener { FetchService.startFetch(context) }

        fetchFinishedBroadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                rootView.swipe_to_refresh_layout.isRefreshing = false
            }
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        presenter!!.onStart((activity.application as StreetArtViewApp))

        val intentFilter = IntentFilter()
        intentFilter.addAction(FetchService.EVENT_FETCH_FINISHED)
        LocalBroadcastManager.getInstance(context).registerReceiver(fetchFinishedBroadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        presenter!!.onStop()
        LocalBroadcastManager.getInstance(context).unregisterReceiver(fetchFinishedBroadcastReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = StreetArtViewApp.getRefWatcher(activity);
        refWatcher.watch(this);
    }

    override fun showArtObjects(artObjectUis: List<ArtObjectUi>) {
        adapter!!.setData(artObjectUis)
    }

    override fun applyFilter(queryToApply: String) {
        presenter!!.applyFilter(queryToApply)
    }

    companion object {
        fun newInstance(): ArtListFragment {
            val fragment = ArtListFragment()
            return fragment
        }
    }
}
