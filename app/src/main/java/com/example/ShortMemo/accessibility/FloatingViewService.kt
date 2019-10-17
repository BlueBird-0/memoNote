package com.example.ShortMemo.accessibility

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.graphics.PixelFormat
import com.example.ShortMemo.R
import android.content.Context
import android.os.Build
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ShortMemo.MainActivity


class FloatingViewService : Service() {
    lateinit var mWindowManager : WindowManager
    lateinit var mFloatingView : View

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)

        //Add the view to the window.

        val LAYOUT_FLAG: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)

        //Specify the view position
        params.gravity = Gravity.TOP or Gravity.LEFT        //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100

        //Add the view to the window
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatingView, params)


        //touch & drag function
        var constraintLayout = mFloatingView.findViewById<ConstraintLayout>(R.id.collapsedLayout)

        constraintLayout.setOnClickListener{
            var intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext?.startActivity(intent)
        }

        var initialX : Int = params.x
        var initialY : Int = params.y
        var initialTouchX : Float = 0F
        var initialTouchY : Float = 0F
        constraintLayout.setOnTouchListener(View.OnTouchListener { v, event ->
            when(event.action)
            {
                MotionEvent.ACTION_DOWN ->{
                    //remember the initial position.
                    initialX = params.x
                    initialY = params.y

                    //get the touch location
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    //Calculate the X and Y coordinates of the view.
                    params.x = initialX + (event.getRawX() - initialTouchX).toInt()
                    params.y = initialY + (event.getRawY() - initialTouchY).toInt()

                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(mFloatingView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    mWindowManager.updateViewLayout(mFloatingView, params)
                }
            }
            false
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        if(mFloatingView != null) mWindowManager.removeView(mFloatingView)
    }
}