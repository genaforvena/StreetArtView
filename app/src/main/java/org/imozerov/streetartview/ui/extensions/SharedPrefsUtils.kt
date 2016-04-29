package org.imozerov.streetartview.ui.extensions

import android.content.SharedPreferences

/**
 * Created by imozerov on 29.04.16.
 */
fun SharedPreferences.putDouble(key : String, value: Double) {
    edit().putLong(key, java.lang.Double.doubleToRawLongBits(value)).apply();
}

fun SharedPreferences.getDouble(key : String, defaultValue : Double) : Double {
    return java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToLongBits(defaultValue)));
}