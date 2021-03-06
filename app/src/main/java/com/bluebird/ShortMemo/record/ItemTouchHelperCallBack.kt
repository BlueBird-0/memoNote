package com.bluebird.ShortMemo.record

import android.graphics.Canvas
import androidx.core.content.ContextCompat
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import android.util.TypedValue
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class ItemTouchHelperCallBack(val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if(viewHolder is FooterViewHolder) {
            val dragFlags = 0
            val swipeFlags = 0
            return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
        }else {
            val dragFlags = 0
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
        }
    }


    //스와이프시 그림 그리기
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addBackgroundColor(ContextCompat.getColor(recyclerView.context, com.bluebird.ShortMemo.R.color.colorPrimaryDark))
                .addSwipeRightActionIcon(com.bluebird.ShortMemo.R.drawable.ic_delete)
                .addSwipeLeftActionIcon(com.bluebird.ShortMemo.R.drawable.ic_delete)
                .setIconHorizontalMargin(TypedValue.COMPLEX_UNIT_DIP, 20)
                .create()
                .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        adapter.onItemMoved(p1.adapterPosition, p2.adapterPosition)
        return true
    }


    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
        adapter.onItemDismiss(p0.adapterPosition)
    }


    interface ItemTouchHelperAdapter {
        fun onItemMoved(fromPosition: Int, toPosition: Int)

        fun onItemDismiss(position: Int)
    }
}

