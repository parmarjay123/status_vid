package com.example.boozzapp.activities

import android.content.Context
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
import com.example.boozzapp.adscontrollers.NativeAdItem
import com.example.boozzapp.controls.CustomDialog
import com.example.boozzapp.pojo.CategoryList
import com.example.boozzapp.pojo.HomeCategoryPojo
import com.example.boozzapp.pojo.HomeTemplate
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_setting.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeActivity : BaseActivity() {
    var totalPage = 1
    var page = 1
    lateinit var adapter: HomeTemplatesAdapter
    var homeCategoryList = ArrayList<CategoryList?>()

    //var list = ArrayList<ExploreTemplatesItem?>()
    var sortBy = "newest"
    val updatedList: MutableList<Any?> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        activity = this
        storeUserData = StoreUserData(activity)

        val adRequest = AdRequest.Builder().build()
        homeBannerAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adHomeLoadingText.isVisible = false
                homeBannerAdView.isVisible = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                adHomeLoadingText.isVisible = false
                homeBannerAdView.isVisible = true

            }
        }
        homeBannerAdView.loadAd(adRequest)

        swipeRefreshLayout.setOnRefreshListener {
            page = 1
            homeCategories()
        }

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

        llSearch.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }
        llSetting.setOnClickListener {
            startActivity(Intent(activity, SettingActivity::class.java))
        }

        llRandom.setOnClickListener {
            sortBy = "random"
            page = 1
            homeTemplateList(sortBy)
            ivClose.performClick()

        }

        llNew.setOnClickListener {
            sortBy = "newest"
            page = 1
            homeTemplateList(sortBy)
            ivClose.performClick()

        }

        llOldest.setOnClickListener {
            sortBy = "oldest"
            page = 1
            homeTemplateList(sortBy)
            ivClose.performClick()

        }
        llPopular.setOnClickListener {
            sortBy = "popular"
            page = 1
            homeTemplateList(sortBy)
            ivClose.performClick()
        }

        val watermark_path: String = getZipDirectoryPath(activity) + getString(R.string.watermark)
        val watermark_file = File(watermark_path)
        if (!watermark_file.exists()) {
            try {
                generateWatermark()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        homeCategories()

    }

    override fun onBackPressed() {
        val alert = CustomDialog(activity)
        alert.setCancelable(false)
        alert.show()
        alert.setTitle("Exit App")
        alert.setMessage("Are you sure want to Exit App?")
        alert.setPositiveButton("Yes") {
            alert.dismiss()
            super.onBackPressed()
        }
        alert.setNegativeButton("No") {
            alert.dismiss()
        }


    }

    @Throws(IOException::class)
    private fun generateWatermark() {
        val file = getZipDirectoryPath(activity) + getString(R.string.watermark)
        try {
            val inputStream = assets.open(getString(R.string.watermark))
            inputStream.use { inputStream ->
                val outputStream = FileOutputStream(file)
                outputStream.use { outputStream ->
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(buf).also { it.also { len = it } } > 0) {
                        outputStream.write(buf, 0, len)
                    }
                }
            }
        } catch (e: IOException) {
            throw IOException("Could not open robot png", e)
        }
    }


    private fun getZipDirectoryPath(mContext: Context): String? {
        val externalDirectory = mContext.filesDir.absolutePath
        val dir = File(
            externalDirectory + File.separator +
                    mContext.resources.getString(R.string.zip_directory)
        )
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath + File.separator
    }


    private fun homeCategories() {
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
                homeCategoryList.add(CategoryList("", "Explore", 0))
                categoryPojo.data?.let { homeCategoryList.addAll(it) }

                val categoryAdapter = HomeCategoryAdapter(
                    activity,
                    homeCategoryList,
                    sortBy
                )
                rvCategories.adapter = categoryAdapter
                homeTemplateList(sortBy)


            }

            override fun onError(code: Int, error: String) {
                homeTemplateList(sortBy)
                Log.i("Error", error)
            }


        })
    }

    private fun homeTemplateList(sort_by: String) {
        swipeRefreshLayout?.isRefreshing = false

        if (page == 1)
            showProgress()

        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> = retrofitHelper.api().homeTemplates(sort_by, page)

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {

                if (page == 1)
                    dismissProgress()

                val responseString = body.body()!!.string()
                val pojo = Gson().fromJson(responseString, HomeTemplate::class.java)
                totalPage = pojo.data!!.total_page!!.toInt()

                if (page == 1) {
                    updatedList.clear()
                    for ((index, template) in pojo.data.templates!!.withIndex()) {
                        template?.let { updatedList.add(it) }
                        if ((index + 1) % 4 == 0 && index != pojo.data.templates.size - 1) {
                            updatedList.add(NativeAdItem()) // Add a marker for the native ad
                        }
                    }
                    adapter = HomeTemplatesAdapter(activity, updatedList, rvHomeList)
                    activity.rvHomeList.adapter = adapter

                    adapter.setOnLoadMoreListener(object : HomeTemplatesAdapter.OnLoadMoreListener {
                        override fun onLoadMore() {
                            if (page < totalPage) {
                                updatedList.add(null)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    adapter.notifyItemInserted(updatedList.size - 1)
                                    adapter.notifyItemRangeChanged(
                                        updatedList.size - 1,
                                        updatedList.size
                                    )
                                    page += 1
                                    homeTemplateList(sort_by)
                                }, 1000)
                            }
                        }
                    })
                } else {
                    val newItems = mutableListOf<Any?>()

                    for ((index, template) in pojo.data.templates!!.withIndex()) {
                        template?.let { newItems.add(it) }
                        if ((index + 1) % 4 == 0 && index != pojo.data.templates.size - 1) {
                            newItems.add(NativeAdItem()) // Add a marker for the native ad
                        }
                    }

                    updatedList.removeAt(updatedList.size - 1) // Remove the loading item
                    updatedList.addAll(newItems) // Add new data
                    adapter.notifyDataSetChanged() // Notify data change

                    adapter.setLoaded()
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