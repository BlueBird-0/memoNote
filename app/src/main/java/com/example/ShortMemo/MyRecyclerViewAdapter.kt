package com.example.ShortMemo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

// 아이템 리스트
class ItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var titleView: TextView = itemView.item_content
    fun bind(beer: ViewModel) =
            with(itemView) {
                val noteViewModel = beer as NoteViewModel
                titleView.text = noteViewModel.note.content
            }
}
class FooterViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
}

class MyRecyclerViewAdapter(private val context: Context, private val items: MutableList<ViewModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        ItemTouchHelperCallBack.ItemTouchHelperAdapter {
    private val TYPE_ITEM = 1
    private val TYPE_FOOTER = 2


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ItemViewHolder) {
            holder.bind(items[position])
        }else {
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_ITEM) {
            val mainView : View = LayoutInflater.from(parent!!.context).inflate(R.layout.list_item, parent, false)
            return ItemViewHolder(mainView)
        }else {
            val footerView : View = LayoutInflater.from(parent!!.context).inflate(R.layout.list_footer, parent, false)
            return FooterViewHolder(footerView)
        }

        //ItemViewHolder(parent.inflate(R.layout.list_item))
    }

    override fun getItemViewType(position: Int): Int {
        if(isPositionFooter(position)){
            return TYPE_FOOTER
        } else {
            return TYPE_ITEM
        }
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) = swapItems(fromPosition, toPosition)

    override fun onItemDismiss(position: Int) = deleteItem(position)


    private fun deleteItem(position: Int) {
        items.removeAt(position)
        FeedReaderDbHelper.checkData(context, position+1)

        notifyItemRemoved(position)
    }


    private fun swapItems(positionFrom: Int, positionTo: Int) {
        Log.d("test001", "PositionFrom: "+ positionFrom)
        Log.d("test001", "PositionTo : " + positionTo)
        //Collections.swap(items, positionFrom, positionTo)
        FeedReaderDbHelper.swapData( context, positionFrom, positionTo)
        notifyItemMoved(positionFrom, positionTo)
    }

    fun ViewGroup.inflate(layoutRes: Int): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
    }

    fun isPositionFooter(position: Int): Boolean{
        return position == items!!.size
    }
}