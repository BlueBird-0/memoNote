package com.example.ShortMemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

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


            val note = Note(0, sstResult?.get(0))
            FeedReaderDbHelper.writeData(context, note)
            var addedNote = NoteViewModel(note)

            MainActivity.list.add(addedNote)
            note_list.adapter?.notifyDataSetChanged()
        }
        Log.d("test001", sstResult.toString())

        popupClass.closePopupWindow()
    }
}