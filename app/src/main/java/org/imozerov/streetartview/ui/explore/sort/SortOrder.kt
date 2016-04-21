package org.imozerov.streetartview.ui.explore.sort

import android.content.SharedPreferences
import android.util.Log
import org.imozerov.streetartview.BuildConfig
import org.imozerov.streetartview.R

/**
 * Created by imozerov on 21.04.16.
 */
val TAG = "SortOrder"

object SortOrder {
    val KEY = "sort_order"
    val byDate = 1
    val byDistance = 2

    fun getString(sortOrder: Int): Int {
        if (sortOrder == byDate) {
            return R.string.toast_sort_by_date
        } else if (sortOrder == byDistance) {
            return R.string.toast_sort_by_distance
        } else {
            val errorMsg = "Unknown sort order $sortOrder"
            if (BuildConfig.DEBUG) {
                throw RuntimeException(errorMsg)
            }
            Log.e(TAG, errorMsg)
        }
        return R.string.toast_sort_by_date
    }
}

fun SharedPreferences.swapSortOrder(): Int {
    val currentOrder = getSortOrder()
    val newSortOrder: Int;
    if (currentOrder == SortOrder.byDate) {
        newSortOrder = SortOrder.byDistance
    } else {
        newSortOrder = SortOrder.byDate
    }
    edit().putInt(SortOrder.KEY, newSortOrder).apply()
    return newSortOrder
}

fun SharedPreferences.getSortOrder(): Int {
    return getInt(SortOrder.KEY, SortOrder.byDate)
}