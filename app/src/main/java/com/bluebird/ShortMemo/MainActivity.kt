package com.bluebird.ShortMemo

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import com.bluebird.ShortMemo.accessibility.BackgroundService
import com.bluebird.ShortMemo.accessibility.WidgetProvider
import com.bluebird.ShortMemo.record.RecordActivity
import com.bluebird.ShortMemo.tutorial.TutorialActivity
import com.bluebird.ShortMemo.write.WriteActivity

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.adView
import kotlinx.android.synthetic.main.activity_main.btn_rec
import kotlinx.android.synthetic.main.activity_main.btn_set
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(){
    companion object {
        val PERMISSIONS_REQUEST_CODE = 1000
        val WRITE_NOTE_REQUEST_CODE = 1001
        val UPDATE_NOTE_REQUEST_CODE = 1002
        var list = mutableListOf<ViewModel>()

    }

    lateinit var mRecognizer: SpeechRecognizer
    lateinit var audioIntent: Intent

    private fun tutorialCheck() {
        val sharedPref = this.getSharedPreferences(getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
        var showsTutorial = sharedPref.getBoolean(getString(R.string.option_tutorial), false)

        if(showsTutorial == false) {
            startActivity(Intent(applicationContext, TutorialActivity::class.java))
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //tutorial
        tutorialCheck()
        //initialize
        checkPermissions()
        //ADDMOB
        MobileAds.initialize(this, "ca-app-pub-8004776504808264~7066223224")
        setAdView()
        //testcode
        //TODO Notification
        Log.d("Test001_service", "isServiceRunning : " + isServiceRunning(BackgroundService::class.java))
        if(! isServiceRunning(BackgroundService::class.java)) {
            startService(Intent(applicationContext, BackgroundService::class.java))
        }


        setRecognizer() //음성 인식 세팅
        initSwi()
        readNotes()

        fab.setOnClickListener{}
        var touchListener = View.OnTouchListener{ view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_UP) {
                if( motionEvent.x>= 0 && motionEvent.x<=fab.width &&
                        motionEvent.y>= 0 && motionEvent.y<=fab.height ) {
                    fab_plus()  //추가 버튼 클릭
                }else if(motionEvent.y < 0-fab.height*1.5 || motionEvent.y > fab.height * 1.5) {
                    fab_cam.visibility = View.GONE
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

                val fadeIn:Animation = AnimationUtils.loadAnimation(this, R.anim.fadein_button)
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


        btn_rec()
        btn_set()
    }

    override fun onResume() {
        super.onResume()
        note_list.adapter?.notifyDataSetChanged()
    }

    private fun isServiceRunning(serviceClass : Class<*>): Boolean {
        var manager = this.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE) ) {
            if(serviceClass.name == service.service.className)
                return true
        }
        return false
    }


    private fun btn_rec() {
        btn_rec.setOnClickListener {
            val recordActivity = Intent(this, RecordActivity::class.java)
            startActivityForResult(recordActivity, UPDATE_NOTE_REQUEST_CODE)
//            startActivityForResult(writeIntent, WRITE_NOTE_REQUEST_CODE)
            overridePendingTransition(R.anim.left_activity, R.anim.hold_activity)
        }
    }
    private fun btn_set() {
        btn_set.setOnClickListener {
            val optionActivity = Intent(this, OptionActivity::class.java)
            startActivity(optionActivity)
//            startActivityForResult(writeIntent, WRITE_NOTE_REQUEST_CODE)
            overridePendingTransition(R.anim.right_activity, R.anim.hold_activity)
        }
    }


    private fun readNotes() {
        list.removeAll(list)
        /* db 데이터 읽어오기 */
        val dbHelper = FeedReaderDbHelper(applicationContext)
        var db = dbHelper.readableDatabase

        val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMNS_NOTE_CONTENT, FeedEntry.COLUMNS_NOTE_CREATED_TIME, FeedEntry.COLUMNS_NOTE_CHECKED_TIME, FeedEntry.COLUMNS_NOTE_PICTURE_URI)
        val selection = "${FeedEntry.COLUMNS_NOTE_CHECKED_TIME} IS NULL"
        val sortOrder = "${FeedEntry.COLUMNS_NOTE_CREATED_TIME} ASC"

        val cursor = db.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,//selection,              // The columns for the WHERE clause
                null,     // The values for the WHERE clause
                null,         // don't group the rowss
                null,           // don't filter by row groups
                sortOrder               // The sort order
        )

        val itemIds = mutableListOf<Long>()
        with(cursor){
            Log.d("MainActivity", "커서 시작됨")
            while(moveToNext()){
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                itemIds.add(itemId)

                Log.d("MainActivity", "ID : "+cursor.getLong(getColumnIndex("${BaseColumns._ID}")))
                Log.d("MainActivity", "CONTENT : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}")))
                Log.d("MainActivity", "CREATED_TIME : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
                Log.d("MainActivity", "CHECKED_TIME : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
                Log.d("MainActivity", "PICTURE_URI : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}")))

                val id = cursor.getLong(getColumnIndex("${BaseColumns._ID}"))
                val content = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}"))
                val createdTime : Date? = FeedReaderDbHelper.sdf.parse(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
                var checkedTime : Date?
                if (cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")) == null) {
                    checkedTime = null
                }else {
                    checkedTime = FeedReaderDbHelper.sdf.parse(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
                }
                var pictureUri = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}"))

                var noteVM = NoteViewModel(Note(id, content, createdTime, checkedTime, pictureUri))
                //val pictureUri = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}"))
                list.add(noteVM)
            }
        }
    }

    private fun checkPermissions() {
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                val permissions : Array<String> = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions,  PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun setAdView() {
        val sharedPref = this.getSharedPreferences(getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
        if(sharedPref.getBoolean(getString(R.string.option_windowShortcut), false) == false) {
            Log.d("test001_adView", "광고 노출")
            val adRequest = AdRequest.Builder()
//                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                    .addTestDevice("D7C25E2ED3376194EC9396F596C65900")
                    .build()

            adView.loadAd(adRequest)
            adView.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        var checked = false
        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) ||
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) )
        {
            checked = true
        }
        if(requestCode == PERMISSIONS_REQUEST_CODE) {
            if(grantResults.size > 0){
                for (grantResult in grantResults){
                    if (grantResult != PackageManager.PERMISSION_GRANTED)
                    {
                        if (checked == false) {//checked [ Don't Ask Again ]
                            Toast.makeText(applicationContext, "잠시 후에 설정창으로 이동합니다. '권한'에서 권한을 허용해 주세요", Toast.LENGTH_LONG).show()
                            Handler().postDelayed(Runnable {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }, 3000)
                        }else {
                            AlertDialog.Builder(this)
                                    //.setTitle(R.string.app_name)
                                    .setTitle("알림")
                                    .setMessage("앱 사용을 위해 권한이 필요합니다")
                                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                                        this@MainActivity.finish()
                                    })
                                    .show()
                        }
                    }
                }
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun fab_plus() {
        val writeIntent = Intent(this, WriteActivity::class.java)
        startActivityForResult(writeIntent, WRITE_NOTE_REQUEST_CODE)
        overridePendingTransition(R.anim.open_activity, R.anim.hold_activity)
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
                        val sdf : SimpleDateFormat = SimpleDateFormat(getString(R.string.sdf_note))
                        val content = sdf.format(Date())
                        uriList as ArrayList<Uri>
                        val note = Note(0, content, Date(), null, uriList)
                        note.id = FeedReaderDbHelper.writeData(applicationContext, note)
                        list.add(NoteViewModel(note))
                        note_list.adapter?.notifyDataSetChanged()


                        //widgetUpdate 위젯 새로고침
                        val ids = AppWidgetManager.getInstance(applicationContext).getAppWidgetIds(ComponentName(applicationContext, WidgetProvider::class.java))
                        AppWidgetManager.getInstance(applicationContext).notifyAppWidgetViewDataChanged(ids, R.id.widgetListView)
                    }
                })
    }


    val popupclass = PopupClass()
    private fun fab_mic() {
        mRecognizer.startListening(audioIntent)
    }

    private fun setRecognizer(){
        audioIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        audioIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        audioIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        audioIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        audioIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말해주세요")
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)

//        mRecognizer.setRecognitionListener(MyRecognitionListener())
        mRecognizer.setRecognitionListener(MyRecognitionListener(popupclass, mainLayout, applicationContext, note_list))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            WRITE_NOTE_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK)
                {
                    var addedNote = NoteViewModel(data!!.getParcelableExtra<Note>("note"))
                    list.add(addedNote)
                    note_list.adapter?.notifyDataSetChanged()
                }
            }
            UPDATE_NOTE_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK)
                {
                    var addedNote = NoteViewModel(data!!.getParcelableExtra<Note>("note"))
                    Log.d("test002 ", addedNote.note.content)
                    Log.d("test002 ", ""+addedNote.note.id)
                    Log.d("test002 ", ""+addedNote.note.pictureUri)
                    //list.removeAll(list)
                    var updateNoteIndex = getIndex(addedNote.note.id)

                    list.removeAt(updateNoteIndex)
                    list.add(updateNoteIndex,addedNote)
                    note_list.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun getIndex(id: Long) : Int {
        var index : Int = 0
        for(listItem in list) {
            var noteVM = listItem as NoteViewModel
            if(id == noteVM.note.id)
                break
            index++
        }
        return index
    }

    private fun initSwi(){
        note_list.layoutManager = LinearLayoutManager(this)
        val adapter = MyRecyclerViewAdapter(applicationContext , list)
        note_list.adapter = adapter

        val itemTouchHelperCallBack = ItemTouchHelperCallBack(adapter)
        val touchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        touchHelper.attachToRecyclerView(note_list)
    }
}