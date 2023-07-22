package com.example.boozzapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.boozzapp.R
import com.example.boozzapp.adapter.HomeCategoryAdapter
import com.example.boozzapp.adapter.HomeTemplatesAdapter
import com.example.boozzapp.pojo.CategoryList
import com.example.boozzapp.pojo.HomeCategoryPojo
import com.example.boozzapp.pojo.HomeTemplate
import com.example.boozzapp.pojo.TemplatesItem
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class HomeActivity : BaseActivity() {
    var totalPage = 1
    var page = 1
    lateinit var adapter: HomeTemplatesAdapter
    var homeCategoryList = ArrayList<CategoryList?>()

    var list = ArrayList<TemplatesItem?>()
    var sort_by = "newest"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        activity = this
        storeUserData = StoreUserData(activity)


        tvQuotes.setOnClickListener {
            it.isClickable = false
            activity.startActivity(Intent(activity, QuotesActivity::class.java))
            Handler(Looper.getMainLooper()).postDelayed({
                it.isClickable = true
            }, 500)
        }

        llFilter.setOnClickListener {
            flBottom.isVisible = false
            llBottom.isVisible = true
        }
        llTopArrow.setOnClickListener {
            rvHomeList.smoothScrollToPosition(0)
        }
        rvHomeList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                llTopArrow.isVisible = recyclerView.canScrollVertically(-1)
            }
        })

        ivClose.setOnClickListener {
            flBottom.isVisible = true
            llBottom.isVisible = false
        }

        llSetting.setOnClickListener {
            startActivity(Intent(activity, SettingActivity::class.java))
        }

        llRandom.setOnClickListener {
            sort_by = "random"
            page = 1
            homeTemplateList(sort_by)
            ivClose.performClick()

        }

        llNew.setOnClickListener {
            sort_by = "newest"
            page = 1
            homeTemplateList(sort_by)
            ivClose.performClick()

        }

        llOldest.setOnClickListener {
            sort_by = "oldest"
            page = 1
            homeTemplateList(sort_by)
            ivClose.performClick()

        }
        llPopular.setOnClickListener {
            sort_by = "popular"
            page = 1
            homeTemplateList(sort_by)
            ivClose.performClick()

        }


        homeCategories()

    }

    private fun homeCategories() {
        val retrofitHelper = RetrofitHelper(activity)
        var call: Call<ResponseBody> =
            retrofitHelper.api().homeCategories(
                storeUserData.getString(Constants.USER_TOKEN),
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {

                val responseString = body.body()!!.string()
                Log.i("TAG", "HomeCategories$responseString")
                var categoryPojo = Gson().fromJson(responseString, HomeCategoryPojo::class.java)
                homeCategoryList.add(CategoryList("", "Explore", 0))
                categoryPojo.data?.let { homeCategoryList.addAll(it) }

                var categoryAdapter = HomeCategoryAdapter(
                    activity,
                    homeCategoryList,
                    sort_by
                )
                rvCategories.adapter = categoryAdapter
                homeTemplateList(sort_by)


            }

            override fun onError(code: Int, error: String) {

                Log.i("Error", error.toString())
            }


        })
    }

    private fun homeTemplateList(sort_by: String) {
        if (page == 1)
            showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        var call: Call<ResponseBody> =
            retrofitHelper.api().homeTemplates(
                sort_by, page
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                if (page == 1)
                    dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "homeTemplateList$responseString")

                val pojo =
                    Gson().fromJson(responseString, HomeTemplate::class.java)
                totalPage = pojo.data!!.total_page!!.toInt()

                if (page == 1) {
                    list.clear()
                    list.addAll(pojo.data.templates!!)

                    adapter =
                        HomeTemplatesAdapter(activity, list, rvHomeList)
                    activity.rvHomeList.adapter = adapter
                    adapter.setOnLoadMoreListener(object :
                        HomeTemplatesAdapter.OnLoadMoreListener {
                        override fun onLoadMore() {
                            if (page < totalPage) {
                                list.add(null)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    adapter.notifyItemInserted(list.size - 1)
                                    adapter.notifyItemRangeChanged(list.size - 1, list.size)
                                    page += 1
                                    homeTemplateList(sort_by)

                                }, 1000)
                            }
                        }

                    })
                } else {
                    list.removeAt(list.size - 1)
                    list.addAll(pojo.data.templates!!)

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