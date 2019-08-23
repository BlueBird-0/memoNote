package com.example.testapplication

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*

class NoteAdapter(val items : ArrayList<Note>, val context : Context) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return  items.size
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, position: Int) {
        p0?.content?.text = items[position].content
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val content = view.item_content
    }
}
