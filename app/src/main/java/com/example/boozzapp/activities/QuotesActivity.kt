package com.example.boozzapp.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.boozzapp.R
import com.example.boozzapp.adapter.QuotesCategoryAdapter
import com.example.boozzapp.adapter.QuotesTemplatesAdapter
import com.example.boozzapp.pojo.QuoteCategoryList
import com.example.boozzapp.pojo.QuotesCategory
import com.example.boozzapp.pojo.QuotesTemplate
import com.example.boozzapp.pojo.QuotesTemplatesItem
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
    lateinit var adapter: QuotesTemplatesAdapter
    var quotesCategoryList = ArrayList<QuoteCategoryList?>()
    var list = ArrayList<QuotesTemplatesItem?>()
    var sortBy = "newest"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qutoes)
        activity = this
        storeUserData = StoreUserData(activity)


        ivQuotesBack.setOnClickListener { finish() }

        llQuotesFilter.setOnClickListener {
            flQuotesBottom.isVisible = false
            llQuotesBottom.isVisible = true
        }
        llQuotesTopArrow.setOnClickListener {
            rvQuotesList.smoothScrollToPosition(0)
        }
        rvQuotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                llQuotesTopArrow.isVisible = recyclerView.canScrollVertically(-1)
            }
        })

        ivQuotesClose.setOnClickListener {
            flQuotesBottom.isVisible = true
            llQuotesBottom.isVisible = false
        }
        quotesCategory()
    }


    private fun quotesCategory() {
        showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().quotesCategories(
                storeUserData.getString(Constants.USER_TOKEN),
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "HomeCategories$responseString")
                val categoryPojo = Gson().fromJson(responseString, QuotesCategory::class.java)
                quotesCategoryList.add(QuoteCategoryList("", "Explore", 0))
                categoryPojo.data?.let { quotesCategoryList.addAll(it) }


                val categoryAdapter = QuotesCategoryAdapter(
                    activity,
                    quotesCategoryList,
                    sortBy
                )

                rvQuotesCategory.adapter = categoryAdapter

                quotesTemplateList()
            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("Error", error)
            }


        })
    }

    private fun quotesTemplateList() {
        if (page == 1)
            showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().quotesList(page)

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                if (page == 1)
                    dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "quotesTemplateList$responseString")

                val pojo =
                    Gson().fromJson(responseString, QuotesTemplate::class.java)
                totalPage = pojo.data!!.total_page!!.toInt()
                if (page == 1) {
                    list.clear()
                    list.addAll(pojo.data.templates!!)

                    adapter =
                        QuotesTemplatesAdapter(activity, list, rvQuotesList)
                    activity.rvQuotesList.adapter = adapter
                    adapter.setOnLoadMoreListener(object :
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