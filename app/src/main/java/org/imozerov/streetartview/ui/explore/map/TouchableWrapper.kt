package org.imozerov.streetartview.ui.explore.map;

import android.content.Context;
import android.view.MotionEvent;
import android.view.MotionEvent.*
import android.widget.FrameLayout;

class TouchableWrapper : FrameLayout {

    interface UpdateMapAfterUserInteraction {
        fun onUpdateMapAfterUserInteraction(state: State)
    }

    enum class State {
        UP, DOWN
    }

    var updateMapAfterUserInteraction : UpdateMapAfterUserInteraction? = null

    constructor(context: Context) : super(context) {
        try {
            updateMapAfterUserInteraction = context as UpdateMapAfterUserInteraction
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement UpdateMapAfterUserInteraction");
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {

        when(event!!.action) {
            ACTION_DOWN -> updateMapAfterUserInteraction!!.onUpdateMapAfterUserInteraction(State.DOWN);
            ACTION_UP -> updateMapAfterUserInteraction!!.onUpdateMapAfterUserInteraction(State.UP)
        }

        return super.dispatchTouchEvent(event)
    }
}

