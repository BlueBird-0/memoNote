package com.bluebird.ShortMemo.write

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bluebird.ShortMemo.R
import kotlinx.android.synthetic.main.pager_image_item.view.*

class ImagePagerAdapter(var context: Context, var imageModelList:ArrayList<ImageModel>) : PagerAdapter() {
    var layoutInflater : LayoutInflater
    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        imageModelList?.run { return imageModelList.size }
        return 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == (`object` as LinearLayout)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var itemView = layoutInflater.inflate(R.layout.pager_image_item, container, false)
        var getImageModel = imageModelList.get(position)

        //////Glide
        Glide.with(context).load(getImageModel.imageUrl)
                .into(itemView.image_view)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}