package com.example.demoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val listData: ArrayList<DataImages>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listData[position]
        holder.titleImg.setImageResource(currentItem.titleImg)
        holder.toHeading.text = currentItem.caption
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleImg: ImageView = itemView.findViewById(R.id.cardImage)
        val toHeading: TextView = itemView.findViewById(R.id.cardTitle)

    }
}