package com.example.boozzapp.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.boozzapp.R
import com.example.boozzapp.adapter.CategoryWiseVideoAdapter
import com.example.boozzapp.pojo.HomeTemplate
import com.example.boozzapp.pojo.ExploreTemplatesItem
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_categorywise_video.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class CategoryWiseVideoActivity : BaseActivity() {
    var totalPage = 1
    var page = 1
    lateinit var adapter: CategoryWiseVideoAdapter
    var list = ArrayList<ExploreTemplatesItem?>()
    var sortBy = ""
    private var categoryID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorywise_video)
        activity = this
        storeUserData = StoreUserData(activity)

        ivCategoryBack.setOnClickListener { finish() }

        if (intent.getStringExtra("categoryTitle") != null && intent.getStringExtra("categoryTitle") != "") {
            tvCategoryName.text = intent.getStringExtra("categoryTitle").toString()
        }

        if (intent.getStringExtra("sortBy") != null && intent.getStringExtra("sortBy") != "") {
            sortBy = intent.getStringExtra("sortBy").toString()
        }

        if (intent.getStringExtra("categoryId") != null && intent.getStringExtra("categoryId") != "") {
            categoryID = intent.getStringExtra("categoryId").toString()

        }

        Log.i("TAG", "onCreate:$sortBy ")
        Log.i("TAG", "onCreate:$categoryID ")
        categoryWiseVideo()


    }

    private fun categoryWiseVideo() {
        if (page == 1)
            showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().categoryWiseVideo(
                sortBy, categoryID, page
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                if (page == 1)
                    dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "categoryWiseVideo$responseString")

                val pojo =
                    Gson().fromJson(responseString, HomeTemplate::class.java)
                if (pojo.data?.pageSize.isNullOrEmpty()){
                    return
                }
                totalPage = pojo.data!!.total_page!!.toInt()
                if (pojo.data.templates.isNullOrEmpty()) {
                    // Return if the templates list is null or empty
                    return
                }
                if (page == 1) {
                    list.clear()
                    list.addAll(pojo.data.templates)

                    adapter =
                        CategoryWiseVideoAdapter(activity, list, rvCategoriesWiseVideo)
                    activity.rvCategoriesWiseVideo.adapter = adapter
                    adapter.setOnLoadMoreListener(object :
                        CategoryWiseVideoAdapter.OnLoadMoreListener {
                        override fun onLoadMore() {
                            if (page < totalPage) {
                                list.add(null)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    adapter.notifyItemInserted(list.size - 1)
                                    adapter.notifyItemRangeChanged(list.size - 1, list.size)
                                    page += 1
                                    categoryWiseVideo()

                                }, 1000)
                            }
                        }

                    })
                } else {
                    list.removeAt(list.size - 1)
                    list.addAll(pojo.data.templates)

                    adapter.notifyItemRemoved(list.size - 1)
                    adapter.notifyItemRangeChanged(list.size - 1, list.size)

                }
                adapter.setLoaded()

            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("error", error)

            }
        })
    }

}