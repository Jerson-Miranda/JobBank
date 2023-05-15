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
import com.example.jobbank.model.User
import com.squareup.picasso.Picasso


class UserAdapter(private val itemList: List<User>, private val limit: Boolean) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.tvId_item_users)
        val username: TextView = view.findViewById(R.id.tvName_item_users)
        val speciality: TextView = view.findViewById(R.id.tvSpeciality_item_users)
        val business: TextView = view.findViewById(R.id.tvBusiness_item_users)
        val img: ImageView = view.findViewById(R.id.ivProfile_item_users)
        val img2: ImageView = view.findViewById(R.id.ivBanner_item_users)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.id.text = item.id.toString()
        holder.username.text = item.firstName + " " + item.lastName
        holder.speciality.text = item.speciality
        holder.business.text = " in " + item.business
        Picasso.get()
            .load(item.imageUrl)
            .fit()
            .centerCrop()
            .into(holder.img)
        Picasso.get()
            .load(item.imageUrl2)
            .fit()
            .centerCrop()
            .into(holder.img2)
    }

    override fun getItemCount(): Int {
        return if (limit) {
            if (itemList.size > 8) 8 else itemList.size
        } else {
            itemList.size
        }
    }
}