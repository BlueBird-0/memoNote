package com.bluebird.ShortMemo

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bluebird.ShortMemo.accessibility.WidgetProvider
import com.bluebird.ShortMemo.write.WriteActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.view.*

// 아이템 리스트
class ItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var layout: ConstraintLayout = itemView.item_mainLayout
    private var titleView: TextView = itemView.item_content
    private var itemImageView: FrameLayout = itemView.item_image

    fun bind(beer: ViewModel) =
            with(itemView) {
                val noteViewModel = beer as NoteViewModel
                titleView.text = noteViewModel.note.content
                if(noteViewModel.note.pictureUri != null){
                    itemImageView.visibility = View.VISIBLE
                }else {
                    itemImageView.visibility = View.GONE
                }
                layout.setOnClickListener(View.OnClickListener {
                    Log.d( "Test001_Recycler", "Note_Id:  "+noteViewModel.note.id.toString())
                    Log.d( "Test001_Recycler", "Note_Content:  "+noteViewModel.note.content)
                    Log.d( "Test001_Recycler", "Note_CreateTime:  "+noteViewModel.note.createdTime)
                    Log.d( "Test001_Recycler", "Note_checkedTime:  "+noteViewModel.note.checkedTime)
                    Log.d( "Test001_Recycler", "Note_pictureUri:  "+noteViewModel.note.pictureUri)


                    val writeIntent = Intent(context, WriteActivity::class.java)
                    writeIntent.putExtra(BaseColumns._ID, noteViewModel.note.id)
                    val mainActivity = context as Activity
                    mainActivity.startActivityForResult(writeIntent, MainActivity.UPDATE_NOTE_REQUEST_CODE, null)
                    mainActivity.overridePendingTransition(R.anim.open_activity, R.anim.hold_activity)
                })

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


        //widgetUpdate 위젯 새로고침
        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(ids, R.id.widgetListView)

        notifyItemRemoved(position)
    }


    private fun swapItems(positionFrom: Int, positionTo: Int) {
        if(positionTo  >= MainActivity.list.size) { //리스트 마지막 벗어나는 에러 처리
            return
        }
        Log.d("Test001_Swap", "PositionFrom: "+ positionFrom)
        Log.d("Test001_Swap", "PositionTo : " + positionTo)
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