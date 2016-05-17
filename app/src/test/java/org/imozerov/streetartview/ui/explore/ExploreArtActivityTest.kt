package org.imozerov.streetartview.ui.explore

import android.view.View
import kotlinx.android.synthetic.main.activity_explore_art.*
import org.imozerov.streetartview.BuildConfig
import org.imozerov.streetartview.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAnimationUtils
import org.robolectric.util.ActivityController

/**
 * Created by imozerov on 04.03.16.
 */
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
@RunWith(RobolectricGradleTestRunner::class)
class ExploreArtActivityTest {
    var activityController: ActivityController<ExploreArtActivity>? = null

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(ExploreArtActivity::class.java)
    }

    @Test
    fun subscriptionsCreated_onStart() {
        activityController!!.create().start()

        val activity = activityController!!.get()

        assertNotNull(activity.compositeSubscription)
        assertTrue(activity.compositeSubscription.hasSubscriptions())
    }

    @Test
    fun searchViewIsGoneByDefault() {
        activityController!!.create().start()

        val activity = activityController!!.get()

        assertTrue(activity.search_view.visibility == View.GONE)
    }

    @Test
    fun isAbleToRecreateActivity() {
        activityController!!.create().start().resume().restart()
    }

    @Test
    fun subscriptionsRemoved_onStop() {
        activityController!!.create().start().resume().pause().stop()

        val activity = activityController!!.get()

        assertFalse(activity.compositeSubscription.hasSubscriptions())
    }
}