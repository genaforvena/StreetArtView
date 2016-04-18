package org.imozerov.streetartview.ui.explore.all

import android.os.Bundle
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.ui.explore.base.AbstractListFragment
import org.imozerov.streetartview.ui.explore.base.ArtListPresenter
import javax.inject.Inject

class ArtListFragment : AbstractListFragment() {
    private val TAG = "ArtListFragment"
    override val presenter: ArtListPresenter = AllPresenter()

    @Inject
    lateinit var tracker: Tracker

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity.application as StreetArtViewApp).appComponent.inject(this);
    }

    override fun onStart() {
        super.onStart()

        tracker.setScreenName(TAG)
        tracker.send(HitBuilders.ScreenViewBuilder().build());
    }

    companion object {
        fun newInstance(): ArtListFragment {
            val fragment = ArtListFragment()
            return fragment
        }
    }
}