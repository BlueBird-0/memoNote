package com.bluebird.ShortMemo

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bluebird.ShortMemo.accessibility.WidgetProvider
import com.bluebird.ShortMemo.tutorial.TutorialItemTouchHelperCallBack
import kotlinx.android.synthetic.main.activity_tutorial.view.*
import kotlinx.android.synthetic.main.list_item.view.*

// 아이템 리스트
class TutorialItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                    Toast.makeText(context, R.string.alert_tutorial_longclick, Toast.LENGTH_SHORT).show()
                })

            }
}

class TutorialRecyclerViewAdapter(private val context: Context, private val items: MutableList<ViewModel>, private val mainLayout : ConstraintLayout) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        TutorialItemTouchHelperCallBack.TutorialItemTouchHelperAdapter {
    private var tutorial_longclick : Int = 0       //롱클릭 진행했는지 확인

    private val TYPE_ITEM = 1
    private val TYPE_FOOTER = 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is TutorialItemViewHolder) {
            holder.bind(items[position])
        }else {
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_ITEM) {
            val mainView : View = LayoutInflater.from(parent!!.context).inflate(R.layout.list_item, parent, false)
            return TutorialItemViewHolder(mainView)
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
        if(tutorial_longclick >= 1) {
            items.removeAt(position)
//            FeedReaderDbHelper.checkData(context, position+1)
            notifyItemRemoved(position)

            mainLayout.guideText.text = "기록을 확인합니다"
            mainLayout.note_list_tutorial.elevation = dipToPixels(5f)
            mainLayout.btn_rec_tutorial.elevation = dipToPixels(10f)
            mainLayout.btn_rec_tutorial.setOnClickListener {
                mainLayout.guideText.text = "설정을 변경합니다"
                mainLayout.btn_rec_tutorial.elevation = dipToPixels(5f)
                mainLayout.btn_set_tutorial.elevation = dipToPixels(10f)
            }
            mainLayout.btn_set_tutorial.setOnClickListener {
                mainLayout.guideText.text = "도움말을 끝냅니다."
                mainLayout.btn_set_tutorial.elevation = dipToPixels(5f)
                mainLayout.btn_ok.visibility = View.VISIBLE
                mainLayout.btn_ok.setOnClickListener {
                    val sharedPref = context.getSharedPreferences(context.getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean(context.getString(R.string.option_tutorial), true).commit()
                    System.exit(0)
                }
            }
        }else {
            items[position]
            Toast.makeText(context, R.string.alert_tutorial, Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }


        //widgetUpdate 위젯 새로고침
        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(ids, R.id.widgetListView)
    }


    fun dipToPixels(dipValue : Float) : Float{
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }

    private fun swapItems(positionFrom: Int, positionTo: Int) {
        tutorial_longclick++
        mainLayout.guideText.text = "옆으로 밀어 메모를 삭제합니다"
        Log.d("Test001_Swap", "PositionFrom: "+ positionFrom)
        Log.d("Test001_Swap", "PositionTo : " + positionTo)

        //Collections.swap(items, positionFrom, positionTo)
//        FeedReaderDbHelper.swapData( context, positionFrom, positionTo)
        notifyItemMoved(positionFrom, positionTo)
    }

    fun ViewGroup.inflate(layoutRes: Int): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
    }

    fun isPositionFooter(position: Int): Boolean{
        return position == items!!.size
    }
}