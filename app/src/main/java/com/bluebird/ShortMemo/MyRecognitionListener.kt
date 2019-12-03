package com.bluebird.ShortMemo

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bluebird.ShortMemo.accessibility.WidgetProvider

class MyRecognitionListener(val popupClass: PopupClass, val mainLayout: ConstraintLayout, val context:Context, val note_list: RecyclerView) : RecognitionListener{
    override fun onReadyForSpeech(p0: Bundle?) {
        popupClass.showPopupWindow(mainLayout)
    }

    override fun onRmsChanged(p0: Float) {
    }

    override fun onBufferReceived(p0: ByteArray?) {
    }

    override fun onPartialResults(p0: Bundle?) {
        val data = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val word = data?.get(data.size - 1) as String

        popupClass.setText(word)
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onEndOfSpeech() {
    }

    override fun onError(p0: Int) {
        Toast.makeText(context, R.string.alert_fail_recode, Toast.LENGTH_SHORT).show()
        popupClass.closePopupWindow()
    }

    override fun onResults(p0: Bundle?) {
        var key = SpeechRecognizer.RESULTS_RECOGNITION
        var sstResult = p0?.getStringArrayList(key)
        if(sstResult != null){
            popupClass.setText(sstResult?.get(0))

            //음성인식 데이터 추가
            val note = Note(0, sstResult?.get(0))
            note.id = FeedReaderDbHelper.writeData(context, note)
            val addedNote = NoteViewModel(note)

            MainActivity.list.add(addedNote)
            note_list.adapter?.notifyDataSetChanged()

            //widgetUpdate 위젯 새로고침
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(ids, R.id.widgetListView)
        }

        popupClass.closePopupWindow()
    }
}