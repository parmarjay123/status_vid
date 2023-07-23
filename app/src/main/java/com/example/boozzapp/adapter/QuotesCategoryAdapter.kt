package com.example.boozzapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.boozzapp.R
import com.example.boozzapp.activities.CategoryWiseQuotesActivity
import com.example.boozzapp.activities.ExploreQuotesActivity
import com.example.boozzapp.pojo.QuoteCategoryList
import kotlinx.android.synthetic.main.row_quotes_category.view.*


class QuotesCategoryAdapter(
    val activity: AppCompatActivity,
    var data: ArrayList<QuoteCategoryList?>,
    var sort_by:String

    ) : RecyclerView.Adapter<QuotesCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.row_quotes_category,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pojo = data[position]
        holder.itemView.tvQuotesCatTitle.text = pojo!!.name


        holder.itemView.setOnClickListener {
            if (position == 0) {
                activity.startActivity(Intent(activity, ExploreQuotesActivity::class.java))
            }else{
                activity.startActivity(Intent(activity,CategoryWiseQuotesActivity::class.java)
                    .putExtra("sortBy",sort_by).putExtra("categoryId",pojo.id.toString())
                    .putExtra("categoryTitle",pojo.name.toString()))
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
