package org.imozerov.streetartview.ui.explore

import kotlinx.android.synthetic.main.activity_explore_art.*
import org.imozerov.streetartview.BuildConfig
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
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
    fun dataSourceIsInjected_onCreate() {
        activityController!!.create()

        assertNotNull(activityController!!.get().dataSource)
    }

    @Test
    fun subscriptionsCreated_onStart() {
        activityController!!.create().start()

        val activity = activityController!!.get()

        assertNotNull(activity.compositeSubscription)
        assertTrue(activity.compositeSubscription!!.hasSubscriptions())
    }

    @Test
    fun adapterInstantiated() {
        activityController!!.create()

        assertNotNull(activityController!!.get().viewpager.adapter)
    }

    @Test
    fun subscriptionsRemoved_onStop() {
        activityController!!.create().start().resume().pause().stop()

        val activity = activityController!!.get()

        assertFalse(activity.compositeSubscription!!.hasSubscriptions())
    }
}