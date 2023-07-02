package com.example.boozzapp.adapter


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boozzapp.R
import com.example.boozzapp.activities.PreviewQuotesActivity
import com.example.boozzapp.pojo.QuotesTemplatesItem
import kotlinx.android.synthetic.main.row_home_list.view.*


class QuotesTemplatesAdapter(
    val activity: AppCompatActivity,
    var items: ArrayList<QuotesTemplatesItem?>,
    recyclerView: RecyclerView,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    private var isLoading = false
    private val visibleThreshold = 5
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1


    init {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager!!.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener!!.onLoadMore()
                    }
                    isLoading = true
                }
            }
        })
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    fun setLoaded() {
        isLoading = false
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(activity).inflate(R.layout.row_home_list, parent, false)
            ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val data = items[position]

            Glide.with(activity).load(data!!.imageUrl).into(holder.itemView.ivItem)
            holder.itemView.setOnClickListener {
                activity.startActivity(
                    Intent(activity, PreviewQuotesActivity::class.java)
                        .putExtra("id", data.id).putExtra("imageURL", data.imageUrl)
                )
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    private inner class LoadingViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


    }
}
