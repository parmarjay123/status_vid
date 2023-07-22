package com.example.boozzapp.activities

import android.os.Bundle
import android.util.Log
import com.example.boozzapp.R
import com.example.boozzapp.adapter.HomeCategoryListAdapter
import com.example.boozzapp.pojo.CategoryList
import com.example.boozzapp.pojo.HomeCategoryPojo
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)
        activity = this
        storeUserData = StoreUserData(activity)


        ivVideoCatBack.setOnClickListener { finish() }

        videoCategory()
    }

    private fun videoCategory() {
        showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().homeCategories(
                storeUserData.getString(Constants.USER_TOKEN),
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                dismissProgress()
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
                dismissProgress()
                Log.i("Error", error)
            }


        })
    }


}