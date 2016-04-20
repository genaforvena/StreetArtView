package org.imozerov.streetartview.ui.explore.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_art_list.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.model.ArtObjectUi
import java.util.*

/**
 * Created by imozerov on 21.03.16.
 */
abstract class AbstractListFragment : Fragment(), Filterable, ArtView {
    private val TAG = "AbstractListFragment"

    protected abstract val presenter: ArtListPresenter

    private var rootView: View? = null
    private val adapter by lazy { ArtListAdapter(context, ArrayList<ArtObjectUi>()) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layoutManager = GridLayoutManager(context, 3)
        adapter.setHasStableIds(true)

        rootView = inflater!!.inflate(R.layout.fragment_art_list, container, false)
        rootView!!.art_objects_recycler_view.setHasFixedSize(true)
        rootView!!.art_objects_recycler_view.layoutManager = layoutManager
        rootView!!.art_objects_recycler_view.adapter = adapter

        rootView!!.swipe_to_refresh_layout.setOnRefreshListener { presenter.refreshData() }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        rootView!!.swipe_to_refresh_layout.isRefreshing = false
        presenter.bindView(this, context)
    }

    override fun onStop() {
        super.onStop()
        presenter.unbindView()
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = StreetArtViewApp.getRefWatcher(activity);
        refWatcher.watch(this);
    }

    override fun showArtObjects(artObjectUis: List<ArtObjectUi>) {
        adapter.setData(artObjectUis)
    }

    override fun stopRefresh() {
        rootView?.swipe_to_refresh_layout?.isRefreshing = false
    }

    override fun applyFilter(queryToApply: String) {
        presenter.applyFilter(queryToApply)
    }
}
