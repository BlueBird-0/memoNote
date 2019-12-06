package com.bluebird.ShortMemo.tutorial

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluebird.ShortMemo.*
import kotlinx.android.synthetic.main.activity_tutorial.*
import kotlinx.android.synthetic.main.activity_tutorial.fab
import kotlinx.android.synthetic.main.activity_tutorial.fab_cam
import kotlinx.android.synthetic.main.activity_tutorial.fab_mic


class TutorialActivity : AppCompatActivity() {
    companion object {
        val TUTORIAL_REQUEST_CODE = 1000
        var list = mutableListOf<ViewModel>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        guideText.text = getString(R.string.tutorial_message1)

        fab()
        initSwi()
    }

    fun dipToPixels(dipValue : Float) : Float{
        val metrics = applicationContext.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    @SuppressLint("RestrictedApi")
    fun fab() {
        fab.setOnClickListener{}
        var touchListener = View.OnTouchListener{ view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_UP) {
                if( motionEvent.x>= 0 && motionEvent.x<=fab.width &&
                        motionEvent.y>= 0 && motionEvent.y<=fab.height ) {
                    fab_plus()  //추가 버튼 클릭
                }else if(motionEvent.y < 0-fab.height*1.5 || motionEvent.y > fab.height * 1.5) {
                    fab_cam.visibility= View.GONE
                    fab_mic.visibility = View.GONE
                    true
                }
                else {
                    if (motionEvent.x <= fab.width/2) {
                        fab_mic() // 마이크 버튼 클릭
                    } else if(motionEvent.x > fab.width/2) {
                        fab_cam() // 카메라 버튼 클릭
                    }
                    true
                }
                fab_cam.visibility = View.GONE
                fab_mic.visibility = View.GONE
                true
            }
            else if(motionEvent.action == MotionEvent.ACTION_DOWN){
                fab_cam.visibility = View.VISIBLE
                fab_mic.visibility = View.VISIBLE

                val fadeIn: Animation = AnimationUtils.loadAnimation(this, R.anim.fadein_button)
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
            false
        }
        fab.setOnTouchListener(touchListener)
    }
    fun fab_cam() {
        Toast.makeText(this, R.string.alert_tutorial, Toast.LENGTH_SHORT).show()
    }
    fun fab_mic() {
        Toast.makeText(this, R.string.alert_tutorial, Toast.LENGTH_SHORT).show()
    }
    fun fab_plus() {
        val writeIntent = Intent(this, TutorialWriteActivity::class.java)
        startActivityForResult(writeIntent, TutorialActivity.TUTORIAL_REQUEST_CODE)
        overridePendingTransition(R.anim.open_activity, R.anim.hold_activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            TUTORIAL_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK)
                {
                    var addedNote = NoteViewModel(data!!.getParcelableExtra<Note>("note"))
                    list.add(addedNote)
                    list.add(NoteViewModel(Note(0, getString(R.string.tutorial_message4))))
                    note_list_tutorial.adapter?.notifyDataSetChanged()
                    Log.d("test001_tutorial", "튜토리얼 2번")
                    guideText.text = getString(R.string.tutorial_message3)
                    hint.elevation = dipToPixels(8f)
                    note_list_tutorial.elevation = dipToPixels(10f)
                }
            }
        }
    }

    private fun initSwi(){
        note_list_tutorial.layoutManager = LinearLayoutManager(this)
        val adapter = TutorialRecyclerViewAdapter(applicationContext , TutorialActivity.list, tutorialMainLayout)
        note_list_tutorial.adapter = adapter

        val itemTouchHelperCallBack = TutorialItemTouchHelperCallBack(adapter)
        val touchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        touchHelper.attachToRecyclerView(note_list_tutorial)
    }
}