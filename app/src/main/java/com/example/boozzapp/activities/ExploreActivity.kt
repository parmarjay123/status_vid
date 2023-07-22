package com.example.boozzapp.activities

import android.os.Bundle
import android.util.Log
import com.example.boozzapp.R
import com.example.boozzapp.adapter.ExploreVideoAdapter
import com.example.boozzapp.adapter.HomeCategoryListAdapter
import com.example.boozzapp.pojo.*
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_explore.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ExploreActivity : BaseActivity() {
    var homeCategoryList = ArrayList<CategoryList?>()
    var exploreVideoSuggestionList = ArrayList<ExploreTemplatesItem?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)
        activity = this
        storeUserData = StoreUserData(activity)


        ivVideoCatBack.setOnClickListener { finish() }

        exploreSuggestionList()

    }

    private fun exploreSuggestionList() {
        showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().exploreVideoSuggestions(
                storeUserData.getString(Constants.USER_TOKEN),
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "exploreSuggestionList$responseString")
                val suggestionsPojo = Gson().fromJson(responseString, ExploreVideoPojo::class.java)
                suggestionsPojo.data!!.templates?.let { exploreVideoSuggestionList.addAll(it) }
                val exploreVideoListAdapter = ExploreVideoAdapter(
                    activity,
                    exploreVideoSuggestionList,
                )

                rvExploreVideo.adapter = exploreVideoListAdapter

                  exploreCategoryList()
            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("Error", error)


            }


        })
    }


    private fun exploreCategoryList() {
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().homeCategories(
                storeUserData.getString(Constants.USER_TOKEN),
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                val responseString = body.body()!!.string()
                Log.i("TAG", "HomeCategories$responseString")
                val categoryPojo = Gson().fromJson(responseString, HomeCategoryPojo::class.java)
                categoryPojo.data?.let { homeCategoryList.addAll(it) }
                val homeCategoryListAdapter = HomeCategoryListAdapter(
                    activity,
                    homeCategoryList,
                )

                rvVideoCatList.adapter = homeCategoryListAdapter
            }

            override fun onError(code: Int, error: String) {
                Log.i("Error", error)
            }


        })
    }


}