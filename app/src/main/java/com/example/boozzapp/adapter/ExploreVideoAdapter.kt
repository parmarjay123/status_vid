package com.example.boozzapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boozzapp.R
import com.example.boozzapp.pojo.ExploreTemplatesItem
import kotlinx.android.synthetic.main.row_explore_quote_list.view.*


class ExploreVideoAdapter(
    val activity: AppCompatActivity,
    var data: ArrayList<ExploreTemplatesItem?>,

    ) : RecyclerView.Adapter<ExploreVideoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.row_explore_quote_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pojo = data[position]

        Glide.with(activity).load(pojo!!.thumbnailUrl).into(holder.itemView.ivExploreItem)


    }


    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
