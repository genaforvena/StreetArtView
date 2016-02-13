package org.imozerov.streetartview.ui.explore.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_art_list.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.ui.explore.ArtListPresenter
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.model.ArtObjectUi

class ArtListFragment : Fragment(), Filterable, ArtView {
    private val TAG = "ArtListFragment"

    private var presenter: ArtListPresenter? = null
    private var adapter: ArtListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_art_list, container, false)

        presenter = ArtListPresenter(this)

        adapter = ArtListAdapter(context, java.util.ArrayList<ArtObjectUi>())
        rootView.art_objects_recycler_view.setHasFixedSize(true)
        rootView.art_objects_recycler_view.layoutManager = LinearLayoutManager(context)
        rootView.art_objects_recycler_view.adapter = adapter

        return rootView
    }

    override fun onStart() {
        super.onStart()
        presenter!!.onStart((activity.application as StreetArtViewApp))
    }

    override fun onStop() {
        super.onStop()
        presenter!!.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
