package com.example.testapplication

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper


class ItemTouchHelperCallBack(val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        adapter.onItemMoved(p1.adapterPosition, p2.adapterPosition)
        return true
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
        adapter.onItemDismiss(p0.adapterPosition)    }


    interface ItemTouchHelperAdapter {
        fun onItemMoved(fromPosition: Int, toPosition: Int)

        fun onItemDismiss(position: Int)
    }
}

