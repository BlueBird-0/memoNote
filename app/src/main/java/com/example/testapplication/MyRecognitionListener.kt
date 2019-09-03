package com.example.testapplication

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log

class MyRecognitionListener : RecognitionListener{
    override fun onReadyForSpeech(p0: Bundle?) {
    }

    override fun onRmsChanged(p0: Float) {
    }

    override fun onBufferReceived(p0: ByteArray?) {
    }

    override fun onPartialResults(p0: Bundle?) {
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onEndOfSpeech() {
    }

    override fun onError(p0: Int) {
    }

    override fun onResults(p0: Bundle?) {
        var key = SpeechRecognizer.RESULTS_RECOGNITION
        var sstResult = p0?.getStringArrayList(key)

        //var sstResult = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        Log.d("test001", sstResult?.get(0))
        Log.d("test001", sstResult.toString())
    }
}