package com.example.testapplication

import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import javax.microedition.khronos.opengles.GL10

private class DrawCommand internal constructor(internal val icon: Drawable, internal val backgroundColor: Int)

private fun createDrawaCommand(viewItem: View, dX:Float, iconResId: Int): DrawCommand {
    val context = viewItem.context
    var icon = ContextCompat.getDrawable(context, iconResId)
    icon = icon?.let { DrawableCompat.wrap(it).mutate() }   //  icon = DrawableCompat.wrap (icon) .mutate ()\
    icon?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorAccent),
            PorterDuff.Mode.SRC_IN)
    val backgroundColor = getBackgroundColor(R.color.colorAccent, R.color.colorAccent2, dX, viewItem)
    return DrawCommand(icon!!, backgroundColor)
}

private fun getBackgroundColor(firstColor : Int, secondColor :Int, dX: Float, viewItem: View): Int {
    return when (willActionBeTriggered(dX, viewItem.width)) {
        true -> ContextCompat.getColor(viewItem.context, firstColor)
        false -> ContextCompat.getColor(viewItem.context, secondColor)
    }
}

private fun willActionBeTriggered(dX: Float, viewWidth: Int) : Boolean {
    return Math.abs(dX) >= viewWidth / GL10.GL_POINT_FADE_THRESHOLD_SIZE
}

//배경 그리기
private fun drawBackground( canvas: Canvas, viewItem: View, dX: Float, color: Int) {
    val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    backgroundPaint.color = color
    val backgroundRectangle = getBackgroundRectangle(viewItem, dX)
    canvas.drawRect(backgroundRectangle, backgroundPaint)
}

private fun getBackgroundRectangle(viewItem : View, dX : Float) : RectF {
    return RectF(viewItem.right.toFloat() + dX, viewItem.top.toFloat(),
            viewItem.right.toFloat(), viewItem.bottom.toFloat())
}