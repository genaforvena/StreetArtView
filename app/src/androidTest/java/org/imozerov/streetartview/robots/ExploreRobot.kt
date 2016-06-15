package org.imozerov.streetartview.robots

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.*
import org.imozerov.streetartview.R

/**
 * Created by imozerov on 15.06.16.
 */

class ExploreRobot {
    fun openMap() {
        onView(withChild(withText(R.string.map_fragment_pager_label))).perform(click())
    }

    fun openArtList() {
        onView(withChild(withText(R.string.list_fragment_pager_label))).perform(click())
    }

    fun openFavourites() {
        onView(withChild(withText(R.string.favourites_fragment_pager_label))).perform(click())
        onView(withId(R.id.main_content)).check { view, noMatchingViewException ->
            view
        }
    }
}