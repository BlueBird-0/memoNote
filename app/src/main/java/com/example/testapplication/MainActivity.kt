package com.example.testapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var mRecognizer: SpeechRecognizer
    lateinit var audioIntent: Intent



    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ADDMOB
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)


        setRecognizer() //음성 인식 세팅

        initSwi()




        fab.setOnClickListener{}
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

                if( motionEvent.x>= 0 && motionEvent.x<=fab.width &&
                        motionEvent.y>= 0 && motionEvent.y<=fab.height ) {
                    fab_plus()  //추가 버튼 클릭
                }else if(motionEvent.y < 0-fab.height*1.5 || motionEvent.y > fab.height * 1.5) {
                }
                else {
                    if (motionEvent.x <= fab.width/2) {
                        fab_mic() // 마이크 버튼 클릭
                    } else if(motionEvent.x > fab.width/2) {
                        fab_cam() // 카메라 버튼 클릭
                    }
                    true
                }
                true
            }
            false
        }
        fab.setOnTouchListener(touchListener)
    }


    private fun fab_plus() {
        val intent = Intent(this, WriteActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.open_activity, R.anim.hold_activity)
    }
    private fun fab_mic() {
        //음성인식 권한 체크
        mRecognizer.startListening(audioIntent)
    }
    private fun fab_cam() {
        TedBottomPicker.with(this@MainActivity)
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                //.setSelectedUriList(selectedUriList)
                .showMultiImage(object : TedBottomSheetDialogFragment.OnMultiImageSelectedListener {
                    override fun onImagesSelected(uriList: List<Uri>) {
                        // here is selected image uri list
                    }
                })
    }

    private fun setRecognizer(){
        audioIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        audioIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        audioIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        audioIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말해주세요")
        Toast.makeText(this, "startSpeak!", Toast.LENGTH_LONG).show()
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)

        mRecognizer.setRecognitionListener(MyRecognitionListener())
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
}