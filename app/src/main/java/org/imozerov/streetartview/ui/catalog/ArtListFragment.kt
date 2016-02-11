package org.imozerov.streetartview.ui.catalog

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_art_list.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.DataSource
import org.imozerov.streetartview.ui.interfaces.Filterable
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class ArtListFragment : Fragment(), Filterable {
    private val TAG = "AlertListFragment"

    private var listFilterQuery: String = ""
    private var fetchSubscription: Subscription? = null
    private var adapter: ArtListAdapter? = null

    @Inject lateinit var dataSource: DataSource

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity.application as StreetArtViewApp).storageComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_art_list, container, false)
        val context = context

        adapter = ArtListAdapter(getContext(), java.util.ArrayList<ArtObjectUi>())
        rootView.art_objects_recycler_view.setHasFixedSize(true)
        rootView.art_objects_recycler_view.layoutManager = LinearLayoutManager(context)
        rootView.art_objects_recycler_view.adapter = adapter

        rootView.button.setOnClickListener { v ->
            Log.d(TAG, "adding new ")
            dataSource.addArtObjectStub()
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        fetchSubscription = startFetchingArtObjectsFromDataSource()
    }

    override fun onStop() {
        super.onStop()
        fetchSubscription?.unsubscribe()
    }

    override fun applyFilter(queryToApply: String) {
        listFilterQuery = queryToApply
        fetchSubscription?.unsubscribe()
        fetchSubscription = startFetchingArtObjectsFromDataSource()
    }

    private fun startFetchingArtObjectsFromDataSource(): Subscription {
        return dataSource
                .listArtObjects()
                .map { it.filter { it.matches(listFilterQuery) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { adapter!!.setData(it) }
    }

    companion object {
        fun newInstance(): ArtListFragment {
            val fragment = ArtListFragment()
            return fragment
        }
    }
}
