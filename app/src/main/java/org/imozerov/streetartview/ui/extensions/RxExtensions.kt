package org.imozerov.streetartview.ui.extensions

import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 * Created by imozerov on 18.03.16.
 */
fun CompositeSubscription.addAll(vararg subscriptions: Subscription) {
    subscriptions.forEach {
        add(it)
    }
}
