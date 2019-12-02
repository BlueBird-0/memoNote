package com.example.ShortMemo.record

import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.ShortMemo.*
import com.example.ShortMemo.accessibility.WidgetProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_record.view.*
import kotlinx.android.synthetic.main.list_record_item.view.*
import java.text.SimpleDateFormat

private var prev = object{ var previousTimeStr : String = "" }  //singleton

// 아이템 리스트
class RecordItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mainLayout: ConstraintLayout = itemView.item_record
    private val titleView: TextView = itemView.item_record_content
    private val checkedTime: TextView = itemView.item_record_checkedTime
    private val itemImageView: FrameLayout = itemView.item_record_image

    fun bind(beer: ViewModel, list: MutableList<ViewModel>) {
        with(itemView) {
            val noteViewModel = beer as NoteViewModel
            titleView.text = noteViewModel.note.content
            checkedTime.text = getCheckedTimeText(noteViewModel)
            if (noteViewModel.note.pictureUri != null) {
                itemImageView.visibility = View.VISIBLE
            }
            //click event
            mainLayout.setOnClickListener {
                //Dialog box
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle(R.string.alert_record_title)
                alertDialog.setMessage(R.string.alert_record_message)
                alertDialog.setIcon(R.drawable.ic_return)
                alertDialog.setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialogInterface, i ->

                    var changeNote = noteViewModel.note
                    changeNote.checkedTime = null
                    FeedReaderDbHelper.updateData(context, changeNote, changeNote.id)
                    list.removeAt(adapterPosition)
                    (context as AppCompatActivity).findViewById<RecyclerView>(R.id.note_list).adapter?.notifyDataSetChanged()
                    MainActivity.list.add(noteViewModel)

                    //widgetUpdate 위젯 새로고침
                    val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
                    AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(ids, R.id.widgetListView)
                })
                alertDialog.setNegativeButton(R.string.no, null)
                alertDialog.show()
            }
        }
    }

    //날자별 텍스트 불러오기 (디자인 필요)
    fun getCheckedTimeText(noteViewModel : NoteViewModel) : String {
        //val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS")
        val sdf: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd")
        val checkedTimeStr= sdf.format(noteViewModel.note.checkedTime)

        if(prev.previousTimeStr.equals( checkedTimeStr) == false) {
            prev.previousTimeStr = checkedTimeStr
            return checkedTimeStr
        }

        return ""
    }
}
class FooterViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
}

class RecordRecyclerViewAdapter(private val context: Context, private val items: MutableList<ViewModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        ItemTouchHelperCallBack.ItemTouchHelperAdapter {
    private val TYPE_ITEM = 1
    private val TYPE_FOOTER = 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is RecordItemViewHolder) {
            holder.bind(items[position], items)
        }else {
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_ITEM) {
            val mainView : View = LayoutInflater.from(parent!!.context).inflate(R.layout.list_record_item, parent, false)

            return RecordItemViewHolder(mainView)
        }else {
            val footerView : View = LayoutInflater.from(parent!!.context).inflate(R.layout.list_footer, parent, false)
            return FooterViewHolder(footerView)
        }

        //RecordItemViewHolder(parent.inflate(R.layout.list_item))
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
//        FeedReaderDbHelper.checkData(context, position + 1)

        notifyItemRemoved(position)
    }


    private fun swapItems(positionFrom: Int, positionTo: Int) {
        //Collections.swap(items, positionFrom, positionTo)
        FeedReaderDbHelper.swapData(context, positionFrom, positionTo)
        notifyItemMoved(positionFrom, positionTo)
    }

    fun ViewGroup.inflate(layoutRes: Int): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
    }

    fun isPositionFooter(position: Int): Boolean{
        return position == items!!.size
    }
}