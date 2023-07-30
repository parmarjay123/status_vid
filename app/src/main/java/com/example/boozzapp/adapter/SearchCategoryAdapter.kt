package com.example.boozzapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.boozzapp.R
import com.example.boozzapp.activities.CategoryWiseVideoActivity
import com.example.boozzapp.pojo.CategoryList
import kotlinx.android.synthetic.main.row_search_category.view.*


class SearchCategoryAdapter(
    val activity: AppCompatActivity,
    var data: ArrayList<CategoryList?>,
    var sort_by: String,
) : RecyclerView.Adapter<SearchCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.row_search_category,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pojo = data[position]
        holder.itemView.tvSearchCatName.text = pojo!!.name

        holder.itemView.setOnClickListener {
            activity.startActivity(
                Intent(activity, CategoryWiseVideoActivity::class.java)
                    .putExtra("sortBy", sort_by).putExtra("categoryId", pojo.id.toString())
                    .putExtra("categoryTitle", pojo.name.toString())
            )

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
