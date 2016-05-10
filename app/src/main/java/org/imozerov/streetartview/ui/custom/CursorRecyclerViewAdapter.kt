package org.imozerov.streetartview.ui.custom

import android.content.Context
import android.database.Cursor
import android.database.DataSetObserver
import android.support.v7.widget.RecyclerView

/**
 * Created by imozerov on 10.05.16.
 */
abstract class CursorRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(private val context: Context, private var cursor: Cursor?) : RecyclerView.Adapter<VH>() {
    private var isDataValid: Boolean = false

    private var rowIdColumn: Int = 0

    private val dataSetObserver: DataSetObserver

    init {
        isDataValid = cursor != null
        rowIdColumn = if (isDataValid) this.cursor!!.getColumnIndex("_id") else -1
        dataSetObserver = NotifyingDataSetObserver()
        cursor!!.registerDataSetObserver(dataSetObserver)
    }

    override fun getItemCount(): Int {
        if (isDataValid && cursor != null) {
            return cursor!!.count
        }
        return 0
    }

    override fun getItemId(position: Int): Long {
        if (isDataValid && cursor?.moveToPosition(position) == true) {
            return cursor!!.getLong(rowIdColumn)
        }
        return 0
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        if (!isDataValid) {
            throw IllegalStateException("this should only be called when the cursor is valid")
        }
        if (cursor?.moveToPosition(position) != true) {
            throw IllegalStateException("couldn't move cursor to position " + position)
        }
        onBindViewHolder(viewHolder, cursor!!)
    }

    abstract fun onBindViewHolder(viewHolder: VH, cursor: Cursor)

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    fun changeCursor(cursor: Cursor) {
        val old = swapCursor(cursor)
        old?.close()
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * [.changeCursor], the returned old Cursor is *not*
     * closed.
     */
    fun swapCursor(newCursor: Cursor): Cursor? {
        if (newCursor === cursor) {
            return null
        }
        val oldCursor = cursor
        oldCursor?.unregisterDataSetObserver(dataSetObserver)
        cursor = newCursor
        if (cursor != null) {
            cursor!!.registerDataSetObserver(dataSetObserver)
            rowIdColumn = newCursor.getColumnIndexOrThrow("_id")
            isDataValid = true
            notifyDataSetChanged()
        } else {
            rowIdColumn = -1
            isDataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor
    }

    private inner class NotifyingDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            isDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            isDataValid = false
            notifyDataSetChanged()
        }
    }
}