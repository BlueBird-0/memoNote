package com.example.testapplication

import android.annotation.SuppressLint
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
import android.transition.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView


import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*
import javax.sql.DataSource
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val p = Paint()
    lateinit var mAdView : AdView

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ADDMOB
        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)



        initSwi()
        //initSwipe()


        //Floating action button event
        fab.setOnClickListener { view ->
            val intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.open_activity, R.anim.hold_activity)
        }
        var touchListener = View.OnTouchListener{ view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_DOWN){
                fab_cam.visibility = View.VISIBLE
                fab_mic.visibility = View.VISIBLE

                var fadeIn:Animation = AnimationUtils.loadAnimation(this, R.anim.fadein_button)
                fab_cam.startAnimation(fadeIn)
                fab_mic.startAnimation(fadeIn)
            }
            else if(motionEvent.action == MotionEvent.ACTION_MOVE) {
                if( motionEvent.x>= 0 && motionEvent.x<=fab.width &&
                        motionEvent.y>= 0 && motionEvent.y<=fab.height ) {
                    fab.isPressed = true
                    fab_mic.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
                    fab_cam.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
                }else if(motionEvent.y < 0-fab.height*1.5 || motionEvent.y > fab.height * 1.5) {
                    fab_mic.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
                    fab_cam.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
                }
                else {
                    if (motionEvent.x <= fab.width/2) {
                        fab_mic.backgroundTintList = resources.getColorStateList(R.color.rippleClickedColor)
                        fab_cam.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
                    } else if(motionEvent.x > fab.width/2) {
                        fab_mic.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
                        fab_cam.backgroundTintList = resources.getColorStateList(R.color.rippleClickedColor)
                    }
                    true
                }
            }
            else if(motionEvent.action == MotionEvent.ACTION_UP) {
                fab_cam.visibility = View.GONE
                fab_mic.visibility = View.GONE
                true
            }
            false
        }
        fab.setOnTouchListener(touchListener)
    }

    private fun initSwi(){

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
        val adapter = MyRecyclerViewAdapter(list)
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