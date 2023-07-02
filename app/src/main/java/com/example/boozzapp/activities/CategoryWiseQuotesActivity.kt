package com.example.boozzapp.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.boozzapp.R
import com.example.boozzapp.adapter.QuotesTemplatesAdapter
import com.example.boozzapp.pojo.QuotesTemplate
import com.example.boozzapp.pojo.QuotesTemplatesItem
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_categorywise_quotes.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class CategoryWiseQuotesActivity : BaseActivity() {
    var totalPage = 1
    var page = 1
    lateinit var adapter: QuotesTemplatesAdapter
    var list = ArrayList<QuotesTemplatesItem?>()
    var sortby = ""
    var categoryID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorywise_quotes)
        activity = this
        storeUserData = StoreUserData(activity)

        ivQuotesCategoryBack.setOnClickListener { finish() }

        if (intent.getStringExtra("categoryTitle") != null && intent.getStringExtra("categoryTitle") != "") {
            tvQuotesCategoryName.text = intent.getStringExtra("categoryTitle").toString()
        }

        if (intent.getStringExtra("sortBy") != null && intent.getStringExtra("sortBy") != "") {
            sortby = intent.getStringExtra("sortBy").toString()
        }

        if (intent.getStringExtra("categoryId") != null && intent.getStringExtra("categoryId") != "") {
            categoryID = intent.getStringExtra("categoryId").toString()

        }

        Log.i("TAG", "onCreate:$sortby ")
        Log.i("TAG", "onCreate:$categoryID ")

        quotesTemplateList()


    }

    private fun quotesTemplateList() {
        if (page == 1)
            showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        var call: Call<ResponseBody> =
            retrofitHelper.api().categoryWiseQuotes(
                sortby,categoryID, page,
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                if (page == 1)
                    dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "homeTemplateList$responseString")

                val pojo =
                    Gson().fromJson(responseString, QuotesTemplate::class.java)
                if (pojo.data?.templates.isNullOrEmpty()) {
                    // Return if the templates list is null or empty
                    return
                }


                if (page == 1) {


                    list.clear()
                    list.addAll(pojo.data!!.templates!!)

                    adapter =
                        QuotesTemplatesAdapter(activity, list, rvQuotesCategoriesWiseVideo)
                    activity.rvQuotesCategoriesWiseVideo.adapter = adapter
                    adapter!!.setOnLoadMoreListener(object :
                        QuotesTemplatesAdapter.OnLoadMoreListener {
                        override fun onLoadMore() {
                            if (page < totalPage) {
                                list.add(null)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    adapter.notifyItemInserted(list.size - 1)
                                    adapter.notifyItemRangeChanged(list.size - 1, list.size)
                                    page += 1
                                    quotesTemplateList()

                                }, 1000)
                            }
                        }

                    })
                } else {
                    list.removeAt(list.size - 1)
                    list.addAll(pojo.data!!.templates!!)

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