package com.example.boozzapp.activities

import NativeAdItem
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.adapter.QuotesTemplatesAdapter
import com.example.boozzapp.adscontrollers.InterstitialAdsHandler
import com.example.boozzapp.pojo.QuotesTemplate
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.SessionManager
import com.example.boozzapp.utils.StoreUserData
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_categorywise_quotes.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class CategoryWiseQuotesActivity : BaseActivity() {
    var totalPage = 1
    var page = 1
    lateinit var adapter: QuotesTemplatesAdapter
    var sortBy = ""
    private var categoryID = ""
    val list: MutableList<Any?> = mutableListOf()
    private var activityOpenCount: Int = 0
    private lateinit var sessionManager: SessionManager
    lateinit var interstitialAdsHandler: InterstitialAdsHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorywise_quotes)
        activity = this
        storeUserData = StoreUserData(activity)
        sessionManager = SessionManager(activity)

        showInterestitialSecondTap()
        setupBannerAd()

        ivQuotesCategoryBack.setOnClickListener { finish() }

        if (intent.getStringExtra("categoryTitle") != null && intent.getStringExtra("categoryTitle") != "") {
            tvQuotesCategoryName.text = intent.getStringExtra("categoryTitle").toString()
        }

        if (intent.getStringExtra("sortBy") != null && intent.getStringExtra("sortBy") != "") {
            sortBy = intent.getStringExtra("sortBy").toString()
        }

        if (intent.getStringExtra("categoryId") != null && intent.getStringExtra("categoryId") != "") {
            categoryID = intent.getStringExtra("categoryId").toString()

        }

        Log.i("TAG", "onCreate:$sortBy ")
        Log.i("TAG", "onCreate:$categoryID ")

        quotesTemplateList()



    }

    override fun onResume() {
        super.onResume()
    }

    private fun setupBannerAd() {
        val adRequest = AdRequest.Builder().build()
        quoteCategoryBannerAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adQuoteCategoryLoadingText.isVisible = false
                quoteCategoryBannerAdView.isVisible = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.i("TAG", "onAdFailedToLoad: QuotesCategory${loadAdError.message} ")
                Log.i("TAG", "onAdFailedToLoad: QuotesCategory${loadAdError.code} ")

                adQuoteCategoryLoadingText.isVisible = true
                quoteCategoryBannerAdView.isVisible = false

            }
        }
        quoteCategoryBannerAdView.loadAd(adRequest)
    }

    private fun quotesTemplateList() {
        if (page == 1)
            showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().categoryWiseQuotes(
                sortBy, categoryID, page,
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

                totalPage = pojo.data!!.total_page!!.toInt()
                if (page == 1) {
                    list.clear()
                    for ((index, template) in pojo.data.templates!!.withIndex()) {
                        template?.let { list.add(it) }
                        if ((index + 1) % 6 == 0 && index != pojo.data.templates.size - 1) {
                            list.add(NativeAdItem()) // Add a marker for the native ad
                        }
                    }

                    adapter =
                        QuotesTemplatesAdapter(
                            activity, list, rvQuotesCategoriesWiseVideo,
                            activity.getString(R.string.GL_InQuote_list_Native)
                        )
                    activity.rvQuotesCategoriesWiseVideo.adapter = adapter
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
                    val newItems = mutableListOf<Any?>()
                    for ((index, template) in pojo.data.templates!!.withIndex()) {
                        template?.let { newItems.add(it) }
                        if ((index + 1) % 6 == 0 && index != pojo.data.templates.size - 1) {
                            newItems.add(NativeAdItem()) // Add a marker for the native ad
                        }
                    }

                    list.removeAt(list.size - 1) // Remove the loading item
                    list.addAll(newItems) // Add new data
                    adapter.notifyDataSetChanged() // Notify data change

                    adapter.setLoaded()
                }

            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("error", error)

            }
        })
    }

    private fun showInterestitialSecondTap() {
        if (sessionManager.isNewSession()) {
            storeUserData.setInt(
                com.example.boozzapp.utils.Constants.ADS_COUNT_DASHBOARD_CLICK,
                0
            )
            sessionManager.updateSessionStartTime()
        }
        activityOpenCount =
            storeUserData.getInt(com.example.boozzapp.utils.Constants.ADS_COUNT_DASHBOARD_CLICK)
        if (activityOpenCount == 1) {
            showInterAdsProgress()
            interstitialAdsHandler = InterstitialAdsHandler(
                this,
                getString(R.string.GL_InQuote_List_Inter),
                getString(R.string.FB_InQuote_List_Inter)
            )
            interstitialAdsHandler.loadInterstitialAds()
            interstitialAdsHandler.setAdListener(object :
                InterstitialAdsHandler.InterstitialAdListeners {
                override fun onAdClosed() {
                    Log.i("TAG", "onAdClosed: " + "closed")
                    // Called when the ad is closed
                }

                override fun onAdDismissed() {
                    Log.i("TAG", "onAdClosed: " + "closed")
                    // Called when the ad is dismissed
                }

                override fun onAdLoaded() {
                    dismissInterAdsProgress()

                }

                override fun onError() {
                    dismissInterAdsProgress()

                }
            })

            storeUserData.setInt(
                com.example.boozzapp.utils.Constants.ADS_COUNT_DASHBOARD_CLICK,
                0
            )

        } else {
            activityOpenCount++
            storeUserData.setInt(
                com.example.boozzapp.utils.Constants.ADS_COUNT_DASHBOARD_CLICK,
                activityOpenCount
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::interstitialAdsHandler.isInitialized) {
            interstitialAdsHandler.onDestroy()
        }
    }


}