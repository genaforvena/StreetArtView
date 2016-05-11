package org.imozerov.streetartview.ui.add

import android.content.SharedPreferences
import android.net.Uri

/**
 * Created by imozerov on 11.05.16.
 */
private val IMAGE_URI_KEY = "last_stored_image_uri"

fun SharedPreferences.storeLastImageUri(imageUri: Uri?) {
    edit().putString(IMAGE_URI_KEY, imageUri.toString()).commit()
}

fun SharedPreferences.getLastImageUri() : Uri? {
    val uriString = getString(IMAGE_URI_KEY, null) ?: return null;
    return Uri.parse(uriString)
}