package com.example.boozzapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boozzapp.R
import com.example.boozzapp.pojo.CategoryList
import com.example.boozzapp.pojo.HomeCategoryPojo
import com.example.boozzapp.utils.Constants
import kotlinx.android.synthetic.main.row_category.view.*


class HomeCategoryAdapter(
    val activity: AppCompatActivity,
    var data: ArrayList<CategoryList>,
) : RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.row_category,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pojo = data[position]
        holder.itemView.tvCategoryTitle.text = pojo.name

        Glide.with(activity).load(pojo.imageUrl).into(holder.itemView.ivCategoryImage)


    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
