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


class JobAdapter(private val itemList: List<Job>, private val limit: Boolean) :
    RecyclerView.Adapter<JobAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle_item_job)
        val business: TextView = view.findViewById(R.id.tvBusiness_item_job)
        val address: TextView = view.findViewById(R.id.tvAddress_item_job)
        val time: TextView = view.findViewById(R.id.tvTime_item_job)
        val archive: ImageButton = view.findViewById(R.id.ibArchive_item_job)
        val img: ImageView = view.findViewById(R.id.ivLogo_item_job)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jobs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.business.text = item.business
        holder.address.text = item.address
        holder.time.text = item.time.toString() + " hours ago"
        Picasso.get()
            .load(item.imageUrl)
            .fit()
            .centerCrop()
            .into(holder.img)
        if (item.archive){
            holder.archive.setImageResource(R.drawable.ic_archive_true)
        } else {
            holder.archive.setImageResource(R.drawable.ic_archive_false)
        }
    }

    override fun getItemCount(): Int {
        return if (limit) {
            if (itemList.size > 3) 3 else itemList.size
        } else {
            itemList.size
        }
    }
}