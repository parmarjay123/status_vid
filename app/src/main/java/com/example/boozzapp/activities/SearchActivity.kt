package com.example.boozzapp.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.adapter.HomeTemplatesAdapter
import com.example.boozzapp.adapter.SearchCategoryAdapter
import com.example.boozzapp.adscontrollers.NativeAdItem
import com.example.boozzapp.pojo.*
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.example.boozzapp.utils.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class SearchActivity : BaseActivity() {
    var homeCategoryList = ArrayList<CategoryList?>()
    var sortBy = "newest"
    var totalPage = 1
    var page = 1
    var list = ArrayList<ExploreTemplatesItem?>()
    lateinit var adapter: HomeTemplatesAdapter
    val updatedList: MutableList<Any?> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        activity = this
        storeUserData = StoreUserData(activity)

        searchCategories()

        ivSearchBack.setOnClickListener { finish() }

        ivSearch.setOnClickListener {
            if (Utils.isEmpty(etSearch)) {
                showAlert("Please enter valid value for search")
            } else {
                homeTemplateList(etSearch.text.toString())
            }
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newText = s.toString()
                if (newText.isEmpty()){
                    list.clear()
                    rvSearchCategoryList.isVisible=true
                    rvSearchTemplateList.isVisible=false
                    tvNoDataFound.isVisible=false
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }


    private fun searchCategories() {
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

                val categoryAdapter = SearchCategoryAdapter(
                    activity,
                    homeCategoryList,
                    sortBy
                )
                rvSearchCategoryList.adapter = categoryAdapter
            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("Error", error)
            }


        })
    }

    private fun homeTemplateList(searchText: String) {
        if (page == 1)
            showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().searchData(
                searchText, page
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                if (page == 1)
                    dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "homeTemplateList$responseString")

                val pojo =
                    Gson().fromJson(responseString, SearchPojo::class.java)
                if (pojo.status==false){
                    tvNoDataFound.isVisible=true
                    rvSearchCategoryList.isVisible=true
                    rvSearchTemplateList.isVisible=false

                    list.clear()
                    return
                }else{
                    tvNoDataFound.isVisible=false
                    rvSearchCategoryList.isVisible=false
                    rvSearchTemplateList.isVisible=true
                }
                
                totalPage = pojo.totalPage!!.toInt()


                if (page == 1) {
                    updatedList.clear()
                    for ((index, template) in pojo.data!!.withIndex()) {
                        template?.let { updatedList.add(it) }
                        if ((index + 1) % 4 == 0 && index != pojo.data.size - 1) {
                            updatedList.add(NativeAdItem()) // Add a marker for the native ad
                        }
                    }
                    adapter = HomeTemplatesAdapter(activity, updatedList, rvSearchTemplateList)
                    activity.rvSearchTemplateList.adapter = adapter

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
                                    homeTemplateList(searchText)
                                }, 1000)
                            }
                        }
                    })
                } else {
                    val newItems = mutableListOf<Any?>()

                    for ((index, template) in pojo.data!!.withIndex()) {
                        template?.let { newItems.add(it) }
                        if ((index + 1) %  4== 0 && index != pojo.data.size - 1) {
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