package org.imozerov.streetartview.ui.explore.favourites

import android.os.Bundle
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.ui.explore.base.AbstractListFragment
import javax.inject.Inject

/**
 * Created by imozerov on 21.03.16.
 */
class FavouritesListFragment : AbstractListFragment() {
    private val TAG = "FavouritesListFragment"
    override val presenter = FavouritesPresenter()

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
        fun newInstance(): FavouritesListFragment {
            val fragment = FavouritesListFragment()
            return fragment
        }
    }
}