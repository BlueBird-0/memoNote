package com.example.ShortMemo.write

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ShortMemo.*
import com.example.ShortMemo.accessibility.WidgetProvider
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.activity_write.fab_cam
import kotlinx.android.synthetic.main.activity_write.fab_mic
import java.util.*
import kotlin.collections.ArrayList


class WriteActivity : AppCompatActivity() {
    lateinit var note : Note
    lateinit var mRecognizer: SpeechRecognizer
    lateinit var audioIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        note = Note(0, "")
        // 전달 된 노트 실행
        var id = intent.getLongExtra(BaseColumns._ID, -1)
        if(id != -1L ) {
            note = readNote(id)
            keyboardEdit.setText(note.content)
        }
        imageLoad()


        keyboardEdit.requestFocus()
//        var keyManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        fab_cam.setOnClickListener{ fab_cam() }
        fab_mic.setOnClickListener{ fab_mic() }
        uploadBtn.setOnClickListener{view->
            /* writeNote test code */
            var content = keyboardEdit.text.toString()
            note.content = content
            if(note.id == 0L) { //new data
                note.id = FeedReaderDbHelper.writeData(applicationContext, note)
            }else {
                FeedReaderDbHelper.updateData(applicationContext, note, id)
            }

            //widgetUpdate 위젯 새로고침
            val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(application, WidgetProvider::class.java))
            AppWidgetManager.getInstance(this).notifyAppWidgetViewDataChanged(ids, R.id.widgetListView)

            var returnIntent = Intent()
            returnIntent.putExtra("note", note)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
            overridePendingTransition(R.anim.hold_activity, R.anim.close_activity)
        }
    }

    val popupclass = PopupClass()
    private fun fab_mic() {
        setRecognizer()
        mRecognizer.startListening(audioIntent)
    }

    private fun fab_cam() {
        TedBottomPicker.with(this)
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .setSelectedUriList(note.pictureUri)
                .showMultiImage(object : TedBottomSheetDialogFragment.OnMultiImageSelectedListener {
                    override fun onImagesSelected(uriList: List<Uri>) {
                        note.pictureUri = ArrayList<Uri>()
                        for(uri in uriList) {
                            note.pictureUri!!.add(uri)
                        }
                        imageLoad()
                    }
                })
    }

    private fun setRecognizer(){
        audioIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        audioIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        audioIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        audioIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        audioIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말해주세요")
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)

        mRecognizer.setRecognitionListener(WriteRecognitionListener(popupclass, writeLayout, applicationContext, keyboardEdit))
    }

    private fun imageLoad() {
        var imageTag = 0    //이미지 개수 및 태그번호
        imageLayout.removeAllViews()

        if(note.pictureUri == null || note.pictureUri?.size == 0) {
            fab_cam.backgroundTintList = resources.getColorStateList(R.color.colorPrimaryDark)
            return
        }
        fab_cam.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
        for(uri in note.pictureUri!!) {
            var image = ImageView(applicationContext)

            ///////////////// set params
            var params = image.layoutParams
            if(params == null) {
                params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            image.layoutParams = params
            //////////////////

            Glide.with(applicationContext).load(uri)
                    .override(260)
                    .centerCrop()
                    .into(image)

            image.tag = ++imageTag
            imageLayout.addView(image)

            image.setOnClickListener(View.OnClickListener {
                var intent = Intent(applicationContext, ImagePopupActivity::class.java)
                intent.putExtra("imageTag", image.tag as Int)
                intent.putExtra("note", note)
                startActivity(intent)
            })
        }

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.hold_activity, R.anim.close_activity)
        //super.onBackPressed()
    }

    private fun readNote(id: Long) : Note {
        /* db 데이터 읽어오기 */
        val dbHelper = FeedReaderDbHelper(applicationContext)
        var db = dbHelper.readableDatabase

        val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMNS_NOTE_CONTENT, FeedEntry.COLUMNS_NOTE_CREATED_TIME, FeedEntry.COLUMNS_NOTE_CHECKED_TIME, FeedEntry.COLUMNS_NOTE_PICTURE_URI)
        val selection = "${BaseColumns._ID} = ${id}"

        val cursor = db.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,//selection,              // The columns for the WHERE clause
                null,     // The values for the WHERE clause
                null,         // don't group the rows
                null,           // don't filter by row groups
                null               // The sort order
        )

        val itemIds = mutableListOf<Long>()
        with(cursor){
            moveToNext()
            val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
            itemIds.add(itemId)

            Log.d("Test001_writeActivity", "ID : "+cursor.getLong(getColumnIndex("${BaseColumns._ID}")))
            Log.d("Test001_writeActivity", "CONTENT : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}")))
            Log.d("Test001_writeActivity", "CREATED_TIME : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
            Log.d("Test001_writeActivity", "CHECKED_TIME : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
            Log.d("Test001_writeActivity", "PICTURE_URI : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}")))

            val id = cursor.getLong(getColumnIndex("${BaseColumns._ID}"))
            val content = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}"))
            val createdTime : Date? = FeedReaderDbHelper.sdf.parse(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
            var checkedTime : Date?
            if (cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")) == null) {
                checkedTime = null
            }else {
                checkedTime = FeedReaderDbHelper.sdf.parse(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
            }
            //이미지 불러오기
            var pictureUri : ArrayList<Uri>?
            if(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}")) == "null"){
                pictureUri = null
            }else {
                val pictureUriString = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}"))
                val pictureUriReplace = pictureUriString?.substring(1, pictureUriString.length-1)?.split(", ")
                pictureUri = ArrayList()
                for (str in pictureUriReplace!!) {
                    pictureUri.add(Uri.parse(str))
                }
            }



            var note = Note(id, content, createdTime, checkedTime, pictureUri)
            return note
        }
    }

}