package org.imozerov.streetartview.ui.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.support.v7.widget.SearchView
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.imozerov.streetartview.R

/**
 * Created by imozerov on 29.02.16.
 */
val TAG = "UiExtensions"

fun ImageView.loadImage(imagePath: String) {
    if (imagePath.isNotBlank()) {
        Glide.with(context).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(this)
    } else {
        Glide.with(context).load(R.drawable.einstein).into(this)
    }
}

fun SearchView.animateToGone() {
    animate().translationY(height.toFloat())
            .alpha(0.0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.GONE
                    setQuery("", true)
                }
            });
}

fun SearchView.animateToVisible() {
    animate().translationY(0f)
            .alpha(1f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.VISIBLE
                    isIconified = false
                    requestFocus()
                }
            })
}
