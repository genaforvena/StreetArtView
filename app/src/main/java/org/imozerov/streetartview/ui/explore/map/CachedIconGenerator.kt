package org.imozerov.streetartview.ui.explore.map

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.util.LruCache
import android.text.TextUtils

import com.google.maps.android.ui.IconGenerator

/**
 * Created by imozerov on 31.05.16.
 */
class CachedIconGenerator(context: Context) : IconGenerator(context) {
    private val mBitmapsCache: LruCache<String, Bitmap>
    private var mText: String? = null

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8
        mBitmapsCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, bitmap: Bitmap?): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap!!.byteCount / 1024
            }
        }
    }

    fun makeIcon(text: String): Bitmap {
        mText = text
        return super.makeIcon(text)
    }

    override fun makeIcon(): Bitmap {
        if (TextUtils.isEmpty(mText)) {
            return super.makeIcon()
        } else {
            var bitmap: Bitmap? = mBitmapsCache.get(mText!!)
            if (bitmap == null) {
                bitmap = super.makeIcon()
                mBitmapsCache.put(mText!!, bitmap!!)
            }
            return bitmap
        }
    }
}
