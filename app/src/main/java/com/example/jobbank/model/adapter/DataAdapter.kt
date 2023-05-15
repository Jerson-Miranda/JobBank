package com.example.jobbank.model.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jobbank.R
import com.example.jobbank.model.Job
import com.squareup.picasso.Picasso


class DataAdapter(private val itemList: List<String>) :
    RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val result: TextView = view.findViewById(R.id.tvData_item_job_add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_add, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.result.text = item.toString()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}