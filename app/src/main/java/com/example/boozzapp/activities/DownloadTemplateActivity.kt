package com.example.boozzapp.activities

import android.os.Bundle
import android.util.Log
import com.example.boozzapp.R
import com.example.boozzapp.adapter.ExploreVideoAdapter
import com.example.boozzapp.pojo.ExploreTemplatesItem
import com.example.boozzapp.pojo.ExploreVideoPojo
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_download_template.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class DownloadTemplateActivity : BaseActivity() {
    var downloadVideoSuggestionList = ArrayList<ExploreTemplatesItem?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_template)
        activity = this
        storeUserData = StoreUserData(activity)

        downloadTempBack.setOnClickListener { finish() }

        downloadSuggestionList()

    }

    private fun downloadSuggestionList() {
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
                suggestionsPojo.data!!.templates?.let { downloadVideoSuggestionList.addAll(it) }
                val exploreVideoListAdapter = ExploreVideoAdapter(
                    activity,
                    downloadVideoSuggestionList,
                )

                rvDownloadVideo.adapter = exploreVideoListAdapter


            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("Error", error)


            }


        })
    }
}