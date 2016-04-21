package org.imozerov.streetartview.ui.explore.sort

import android.content.SharedPreferences

/**
 * Created by imozerov on 21.04.16.
 */
object SortOrder {
    val KEY = "sort_order"
    val byDate = 1
    val byDistance = 2
}

fun SharedPreferences.swapSortOrder() : Int {
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

fun SharedPreferences.getSortOrder() : Int {
    return getInt(SortOrder.KEY, SortOrder.byDate)
}