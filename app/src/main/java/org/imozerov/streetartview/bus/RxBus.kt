package org.imozerov.streetartview.bus

import android.util.Log
import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject

/**
 * Created by imozerov on 28.04.16.
 */
class RxBus {
    val TAG = "RxBus"
    val bus = SerializedSubject<Any, Any>(PublishSubject.create());

    fun post(o: Any) {
        Log.v(TAG, "Sending $o")
        bus.onNext(o);
    }

    fun toObservable() : Observable<Any> {
        return bus;
    }
}