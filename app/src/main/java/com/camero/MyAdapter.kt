package com.camero

import androidx.recyclerview.widget.RecyclerView
import android.content.res.ColorStateList
import android.content.Context
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.camero.model.DataCSV
import java.util.*


class MyAdapter// readFileData is passed into the constructor
    :
    RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private var context: Context
    private val csvData: List<DataCSV>

    constructor(Context: Context, csvData: List<DataCSV>) : super() {
        this.context = Context
        this.csvData = csvData
        this.mInflater = LayoutInflater.from(Context)
    }

    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null


    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.list_view_item, parent, false)
        return ViewHolder(view)
    }


    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var tName: TextView? = null
        internal var tPhone: TextView? = null
        internal var fab: FloatingActionButton

        init {
            tName = itemView.findViewById(R.id.name)
            tPhone = itemView.findViewById(R.id.phone)
            fab = itemView.findViewById(R.id.fabx)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }
    }

    // binds the readFileData to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fullName = csvData[position].name
        val phone = csvData[position].phone
        holder.tName?.text = fullName
        holder.tPhone?.text = phone
        val androidColors = context.resources.getIntArray(R.array.androidColors)
        val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]

        holder.fab.backgroundTintList = ColorStateList.valueOf(randomAndroidColor)

    }

    // total number of rows
    override fun getItemCount(): Int {
        return csvData.size
    }


    // convenience method for getting readFileData at click position
    fun getItem(id: Int): String {
        return csvData[id].name
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}