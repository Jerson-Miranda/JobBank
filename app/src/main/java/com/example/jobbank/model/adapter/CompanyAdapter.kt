package com.example.jobbank.model.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jobbank.R
import com.example.jobbank.model.Company
import com.example.jobbank.model.Job
import com.squareup.picasso.Picasso

class CompanyAdapter(private val itemList: List<Company>, private val limit: Boolean) :
    RecyclerView.Adapter<CompanyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName_item_company)
        val speciality: TextView = view.findViewById(R.id.tvSpeciality_item_company)
        val id: TextView = view.findViewById(R.id.tvId_item_company)
        val img: ImageView = view.findViewById(R.id.ivProfile_item_company)
        val img2: ImageView = view.findViewById(R.id.ivBanner_item_company)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_companies, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.name.text = item.name
        holder.speciality.text = item.speciality
        holder.id.text = item.id.toString()
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
            if (itemList.size > 6) 6 else itemList.size
        } else {
            itemList.size
        }
    }
}