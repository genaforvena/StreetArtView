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
import org.imozerov.streetartview.ui.ArtListPresenter
import org.imozerov.streetartview.ui.interfaces.ArtView
import org.imozerov.streetartview.ui.interfaces.Filterable
import org.imozerov.streetartview.ui.model.ArtObjectUi
import javax.inject.Inject

class ArtListFragment : Fragment(), Filterable, ArtView {
    private val TAG = "ArtListFragment"

    private var presenter: ArtListPresenter? = null
    private var adapter: ArtListAdapter? = null

    @Inject lateinit var dataSource: DataSource

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_art_list, container, false)
        val context = context

        adapter = ArtListAdapter(getContext(), java.util.ArrayList<ArtObjectUi>())
        rootView.art_objects_recycler_view.setHasFixedSize(true)
        rootView.art_objects_recycler_view.layoutManager = LinearLayoutManager(context)
        rootView.art_objects_recycler_view.adapter = adapter

        // TODO remove this button!
        rootView.button.setOnClickListener { v ->
            Log.d(TAG, "adding new ")
            dataSource.addArtObjectStub()
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity.application as StreetArtViewApp).storageComponent.inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter = ArtListPresenter(dataSource, this)
        presenter!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter!!.onStop()
        presenter = null
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
