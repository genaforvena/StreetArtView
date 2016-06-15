package org.imozerov.streetartview

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.imozerov.streetartview.robots.ExploreRobot
import org.imozerov.streetartview.ui.explore.ExploreArtActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by imozerov on 15.06.16.
 */
@RunWith(AndroidJUnit4::class)
class NavigationTests {
    @get:Rule
    val activityRule = ActivityTestRule(ExploreArtActivity::class.java);

    @Test
    fun navigationWorks() {
        with (ExploreRobot()) {
            openArtList()
            openFavourites()
            openMap()
        }
    }
}