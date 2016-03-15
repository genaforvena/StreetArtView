package org.imozerov.streetartview.ui.extensions

import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import org.imozerov.streetartview.R

/**
 * Created by imozerov on 29.02.16.
 */
val TAG = "UiExtensions"

fun ImageView.loadImage(imagePath: String) {
    if (imagePath.isNotBlank()) {
        Picasso.with(context)
                .load(imagePath)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(this, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError() {
                        // Try again online if cache failed
                        Picasso.with(context)
                                .load(imagePath)
                                .into(this@loadImage, object : Callback {
                                    override fun onSuccess() {
                                    }

                                    override fun onError() {
                                        Log.w(TAG, "Could not fetch image")
                                    }
                                })
                    }
                })
    } else {
        Picasso.with(context).load(R.drawable.einstein).into(this)
    }
}
