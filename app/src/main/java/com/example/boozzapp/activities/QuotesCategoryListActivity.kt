package com.example.boozzapp.activities

import android.os.Bundle
import android.util.Log
import com.example.boozzapp.R
import com.example.boozzapp.adapter.QuotesCategoryListAdapter
import com.example.boozzapp.pojo.QuoteCategoryList
import com.example.boozzapp.pojo.QuotesCategory
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quotes_category_list.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class QuotesCategoryListActivity : BaseActivity() {
    var quotesCategoryList = ArrayList<QuoteCategoryList?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes_category_list)
        activity = this
        storeUserData = StoreUserData(activity)

        ivQuoteCatBack.setOnClickListener { finish() }


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

                categoryPojo.data?.let { quotesCategoryList.addAll(it) }
                val quotesCategoryListAdapter = QuotesCategoryListAdapter(
                    activity,
                    quotesCategoryList,
                )

                rvQuotesCatList.adapter = quotesCategoryListAdapter
            }
            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("Error", error)
            }


        })
    }

}