package com.example.boozzapp.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.boozzapp.R
import com.example.boozzapp.adapter.HomeTemplatesAdapter
import com.example.boozzapp.adapter.QuotesCategoryAdapter
import com.example.boozzapp.pojo.CategoryList
import com.example.boozzapp.pojo.HomeCategoryPojo
import com.example.boozzapp.pojo.HomeTemplate
import com.example.boozzapp.pojo.TemplatesItem
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_qutoes.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class QuotesActivity : BaseActivity() {
    var totalPage = 1
    var page = 1
    lateinit var adapter: HomeTemplatesAdapter
    var list = ArrayList<TemplatesItem?>()
    var sort_by = "newest"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qutoes)
        activity = this
        storeUserData = StoreUserData(activity)


        ivQuotesBack.setOnClickListener { finish() }

        quotesCategory()
    }


    private fun quotesCategory() {
        showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        var call: Call<ResponseBody> =
            retrofitHelper.api().homeCategories(
                storeUserData.getString(Constants.USER_TOKEN),
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "HomeCategories$responseString")
                var categoryPojo = Gson().fromJson(responseString, HomeCategoryPojo::class.java)


                var categoryAdapter = QuotesCategoryAdapter(
                    activity,
                    categoryPojo.data as ArrayList<CategoryList>,
                )

                rvQuotesCategory.adapter = categoryAdapter

                quotesList(sort_by)
            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("Error", error.toString())
            }


        })
    }

    private fun quotesList(sort_by: String) {
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
                totalPage = pojo.data!!.pageSize!!.toInt()

                if (page == 1) {
                    list.clear()
                    list.addAll(pojo.data.templates!!)

                    adapter =
                        HomeTemplatesAdapter(activity, list, rvQuotesList)
                    activity.rvQuotesList.adapter = adapter
                    adapter!!.setOnLoadMoreListener(object :
                        HomeTemplatesAdapter.OnLoadMoreListener {
                        override fun onLoadMore() {
                            if (page < totalPage) {
                                list.add(null)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    adapter.notifyItemInserted(list.size - 1)
                                    adapter.notifyItemRangeChanged(list.size - 1, list.size)
                                    page += 1
                                    quotesList("newest")

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