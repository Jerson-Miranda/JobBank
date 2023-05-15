package com.example.jobbank.model.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jobbank.R
import com.example.jobbank.model.Notification
import com.squareup.picasso.Picasso

class NotificationAdapter(private val itemList: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle_item_notifications)
        val body: TextView = view.findViewById(R.id.tvBody_item_notifications)
        val time: TextView = view.findViewById(R.id.tvTime_item_notifications)
        val profile: ImageView = view.findViewById(R.id.ivProfile_item_notifications)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notifications, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.body.text = item.body
        holder.time.text = item.time.toString() + " hours ago"
        Picasso.get()
            .load(item.image)
            .fit()
            .centerCrop()
            .into(holder.profile)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}