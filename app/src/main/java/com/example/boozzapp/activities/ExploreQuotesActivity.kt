package com.example.boozzapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.example.boozzapp.R
import com.example.boozzapp.adapter.ExploreQuotesAdapter
import com.example.boozzapp.pojo.ExploreQuotesPojo
import com.example.boozzapp.pojo.ExploreQuotesTemplatesItem
import com.example.boozzapp.pojo.QuoteCategoryList
import com.example.boozzapp.pojo.QuotesCategory
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quotes_category_list.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ExploreQuotesActivity : BaseActivity() {
    var quotesCategoryList = ArrayList<QuoteCategoryList?>()
    var exploreVideoSuggestionList = ArrayList<ExploreQuotesTemplatesItem?>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes_category_list)
        activity = this
        storeUserData = StoreUserData(activity)

        ivQuoteCatBack.setOnClickListener { finish() }


        exploreSuggestionList()
    }

    private fun exploreSuggestionList() {
        showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().exploreQuotesSuggestions(
                storeUserData.getString(Constants.USER_TOKEN),
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "exploreQuotesSuggestionList$responseString")
                val suggestionsPojo = Gson().fromJson(responseString, ExploreQuotesPojo::class.java)
                suggestionsPojo.data?.templates?.let { exploreVideoSuggestionList.addAll(it) }
                val exploreQuotesListAdapter = ExploreQuotesAdapter(
                    activity,
                    exploreVideoSuggestionList,
                )

                rvExploreQuotesVideo.adapter = exploreQuotesListAdapter

                quotesCategory()
            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                quotesCategory()
                Log.i("Error", error)


            }


        })
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
                addChip(categoryPojo.data as ArrayList<QuoteCategoryList>)


            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("Error", error)
            }


        })
    }

    fun addChip(chip: ArrayList<QuoteCategoryList>) {
        for (chip in chip) {
            val chipView =
                LinearLayout.inflate(activity, R.layout.chip_layout, null) as Chip
            chipView.tag = chip
            chipView.text = chip.name
            chipView.setOnClickListener {
                activity.startActivity(
                    Intent(activity, CategoryWiseQuotesActivity::class.java)
                        .putExtra("sortBy", chip.sortBy).putExtra("categoryId", chip.id.toString())
                        .putExtra("categoryTitle", chip.name.toString())
                )
            }

            chipGroup.addView(chipView)
        }
    }


}