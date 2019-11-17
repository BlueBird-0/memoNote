package com.example.ShortMemo.record

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.ShortMemo.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item.view.item_content
import kotlinx.android.synthetic.main.list_record_item.view.*
import java.text.SimpleDateFormat
import java.util.*

private var prev = object{ var previousTimeStr : String = "" }  //singleton

// 아이템 리스트
class RecordItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var titleView: TextView = itemView.item_record_content
    private var checkedTime: TextView = itemView.item_record_checkedTime
    private var itemImageView: FrameLayout = itemView.item_record_image

    fun bind(beer: ViewModel) =
            with(itemView) {
                val noteViewModel = beer as NoteViewModel
                titleView.text = noteViewModel.note.content
                checkedTime.text = getCheckedTimeText(noteViewModel)
                if(noteViewModel.note.pictureUri != null) {
                    itemImageView.visibility = View.VISIBLE
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
            holder.bind(items[position])
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