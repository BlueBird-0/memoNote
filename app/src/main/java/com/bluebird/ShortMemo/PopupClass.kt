package com.bluebird.ShortMemo

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView


class PopupClass {
    private lateinit var popupWindow : PopupWindow
    private lateinit var popupView: View
    //PopupWindow display method
    fun showPopupWindow(view: View) { //Create a View object yourself through inflater
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.popup_recoding, null)
        //Specify the length and width through constants
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        //Make Inactive Items Outside Of PopupWindow
        val focusable = true

        //Create a window with our parameters
        popupWindow = PopupWindow(popupView, width, height, focusable)
        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        //Initialize the elements of our window, install the handler
//        val test2: TextView = popupView.findViewById(R.id.titleText)
//        test2.setText(R.string.textTitle)
//        val buttonEdit: Button = popupView.findViewById(R.id.messageButton)
//        buttonEdit.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) { //As an example, display the message
//                Toast.makeText(view.getContext(), "Wow, popup action button", Toast.LENGTH_SHORT).show()
//            }
//        })
        popupWindow.setOnDismissListener { PopupMenu.OnDismissListener{
            Log.d("test001","팝업창 닫힘")
        } }
        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(OnTouchListener { v, event ->
            //Close the window when clicked
            popupWindow.dismiss()
            true
        })
    }

    fun setText(string:String) {
        val popupMessage = popupView?.findViewById<TextView>(R.id.popup_message)
        popupMessage?.text = string
    }

    fun closePopupWindow() {
        popupWindow.dismiss()
    }

}
