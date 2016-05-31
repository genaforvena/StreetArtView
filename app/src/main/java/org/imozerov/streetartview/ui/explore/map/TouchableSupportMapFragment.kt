package org.imozerov.streetartview.ui.explore.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

class TouchableSupportMapFragment : SupportMapFragment() {

    var mOriginalContentView: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mOriginalContentView = super.onCreateView(inflater, container, savedInstanceState)
        val touchView = TouchableWrapper(activity)
        touchView.addView(mOriginalContentView!!)
        return touchView
    }

    override fun getView(): View? {
        return mOriginalContentView
    }
}