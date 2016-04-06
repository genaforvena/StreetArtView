package org.imozerov.streetartview.ui.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.v7.widget.SearchView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.imozerov.streetartview.R
import pl.droidsonroids.gif.GifDrawable

/**
 * Created by imozerov on 29.02.16.
 */
val TAG = "UiExtensions"

fun ImageView.loadImage(imagePath: String?) {
    if (imagePath?.isNotBlank() == true) {
        val placeholder: Drawable
        if (android.os.Build.VERSION.SDK_INT >= 22) {
            // This animation is really resource consuming!
            // So we can do it only on good devices.
            placeholder = GifDrawable(resources, R.raw.image_loading_placeholder)
            placeholder.start()
        } else {
            placeholder = getDrawableSafely(R.drawable.image_loading_placeholder, context)
        }
        Glide.with(context).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(placeholder)
                .into(this)
    } else {
        Glide.with(context).load(R.drawable.einstein).into(this)
    }
}

private fun getDrawableSafely(id: Int, context: Context) : Drawable {
    if (android.os.Build.VERSION.SDK_INT >= 21) {
        return context.resources.getDrawable(id, context.theme);
    } else {
        return context.resources.getDrawable(id);
    }
}

fun Activity.getDrawableSafely(id: Int): Drawable {
    return getDrawableSafely(id, this)
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
