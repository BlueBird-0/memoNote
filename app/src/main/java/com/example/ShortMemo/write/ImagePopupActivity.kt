package com.example.ShortMemo.write

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ShortMemo.Note
import com.example.ShortMemo.R
import kotlinx.android.synthetic.main.activity_imagepopup.*

class ImagePopupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagepopup)
        var note = intent.getParcelableExtra<Note>("note")
        var imageTag = intent.getIntExtra("imageTag", 0)

        Log.d("test002", "uris : "+note.pictureUri)
        Log.d("test002", "tag " +imageTag)


        var imageModelsArray = ArrayList<ImageModel>()

        for(imageUri in note.pictureUri!!)
        {
            var imageModel = ImageModel(imageUri.toString(), imageTag - 1)
            imageModelsArray.add(imageModel)
        }

        var pager = ImagePagerAdapter(this, imageModelsArray)
        imageViewPager.adapter = pager
        imageViewPager.setCurrentItem(imageTag-1, false)

        //뒤로가기 버튼
        backbutton.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
    }
}