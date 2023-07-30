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
import com.example.boozzapp.pojo.CategoryList
import com.example.boozzapp.pojo.ExploreTemplatesItem
import com.example.boozzapp.pojo.HomeCategoryPojo
import com.example.boozzapp.pojo.HomeTemplate
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
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

    var list = ArrayList<ExploreTemplatesItem?>()
    var sortBy = "newest"

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

        llSearch.setOnClickListener {
            startActivity(Intent(activity,SearchActivity::class.java))
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

    @Throws(IOException::class)
    private fun generateWatermark() {
        val file = getZipDirectoryPath(activity) + getString(R.string.watermark)
        try {
            val inputStream = assets.open(getString(R.string.watermark))
            try {
                val outputStream = FileOutputStream(file)
                try {
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(buf).also { len = it } > 0) {
                        outputStream.write(buf, 0, len)
                    }
                } finally {
                    outputStream.close()
                }
            } finally {
                inputStream.close()
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

                Log.i("Error", error)
            }


        })
    }

    private fun homeTemplateList(sort_by: String) {
        if (page == 1)
            showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
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