package org.imozerov.streetartview.ui.extensions

import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker

/**
 * Created by imozerov on 19.04.16.
 */
fun Tracker.sendScreen(screenName: String) {
    setScreenName(screenName)
    send(HitBuilders.ScreenViewBuilder().build())
}