package com.example.testapplication

import android.arch.lifecycle.ViewModel
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.input.InputManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView


import android.widget.Toast
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*
import javax.sql.DataSource
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val p = Paint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list : ArrayList<Note> = ArrayList()

        /*
        note_list.layoutManager = LinearLayoutManager(this)
        //note_list.layoutManager = GridLayoutManager(this, 2)

        list.add(Note("1번"))
        list.add(Note("2번"))
        list.add(Note("3번"))
        list.add(Note("4번"))
        list.add(Note("1번"))
        list.add(Note("2번"))
        list.add(Note("3번"))
        list.add(Note("4번"))
        list.add(Note("1번"))
        list.add(Note("2번"))
        list.add(Note("3번"))
        list.add(Note("4번"))
        list.add(Note("1번"))
        list.add(Note("2번"))
        list.add(Note("3번"))
        list.add(Note("4번"))
        list.add(Note("1번"))
        list.add(Note("2번"))
        list.add(Note("3번"))
        list.add(Note("4번"))
*/
        //note_list.adapter = NoteAdapter(list, this)

        initSwi()
        //initSwipe()


        //Floating action button event
        fab.setOnClickListener { view ->
            val intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.open_activity, R.anim.hold_activity)
        }

    }

    private fun initSwi(){
        class ItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
            private var titleView: TextView = itemView.item_content

            fun bind(beer: ViewModel) =
                    with(itemView) {
                        val note = beer as Note
                        titleView.text = note.content
                    }


        }
        class ViewAdapter(private val items: MutableList<ViewModel>) : RecyclerView.Adapter<ItemViewHolder>(),
            ItemTouchHelperCallBack.ItemTouchHelperAdapter {
            override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
                    holder.bind(items[position])

            override fun getItemCount(): Int = items.size

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
                    ItemViewHolder(parent.inflate(R.layout.list_item))

            override fun onItemMoved(fromPosition: Int, toPosition: Int) = swapItems(fromPosition, toPosition)

            override fun onItemDismiss(position: Int) = deleteItem(position)

            private fun deleteItem(position: Int) {
                items.removeAt(position)
                notifyItemRemoved(position)
            }

            private fun swapItems(positionFrom: Int, positionTo: Int) {
                Collections.swap(items, positionFrom, positionTo)
                notifyItemMoved(positionFrom, positionTo)
            }

            fun ViewGroup.inflate(layoutRes: Int): View {
                return LayoutInflater.from(context).inflate(layoutRes, this, false)
              }
        }

        val list = mutableListOf<ViewModel>()
        list.add(Note("1번"))
        list.add(Note("2번"))
        list.add(Note("3번"))
        list.add(Note("4번"))
        list.add(Note("1번"))
        list.add(Note("2번"))
        list.add(Note("3번"))
        list.add(Note("4번"))
        list.add(Note("1번"))
        list.add(Note("2번"))
        list.add(Note("3번"))
        list.add(Note("4번"))
        list.add(Note("1번"))
        list.add(Note("2번"))
        list.add(Note("3번"))
        list.add(Note("4번"))
        list.add(Note("1번"))
        list.add(Note("2번"))
        list.add(Note("3번"))
        list.add(Note("4번"))

        note_list.layoutManager = LinearLayoutManager(this)
        val adapter = ViewAdapter(list)
        note_list.adapter = adapter

        val itemTouchHelperCallBack = ItemTouchHelperCallBack(adapter)
        val touchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        touchHelper.attachToRecyclerView(note_list)
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if(direction == ItemTouchHelper.LEFT){
                    //adapter!!.removeItem()
                }else {
                    /*
                    removeView()
                    edit_position = position
                    alertDialog!!.setTitle("Edit Name")
                    et_name!!.setText(names[position])
                    alertDialog!!.show()
                    */
                }
            }
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val icon: Bitmap
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3
                    if(dX > 0) {
                        p.color = Color.parseColor("#388E3C")
                        val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_delete )
                        val icon_dest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    } else {
                        p.color = Color.parseColor("#D32F2F")
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_delete)
                        val icon_dest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(note_list)
    }
}