package com.example.boozzapp.activities

import NativeAdItem
import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.*
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.boozzapp.R
import com.example.boozzapp.adapter.HomeCategoryAdapter
import com.example.boozzapp.adapter.HomeTemplatesAdapter
import com.example.boozzapp.adscontrollers.InterstitialAdsHandler
import com.example.boozzapp.controls.CustomDialog
import com.example.boozzapp.databinding.DialogPrivacyPolicyBinding
import com.example.boozzapp.pojo.CategoryList
import com.example.boozzapp.pojo.HomeCategoryPojo
import com.example.boozzapp.pojo.HomeTemplate
import com.example.boozzapp.rateView.PartyRateDialog
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.android.gms.ads.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean


class HomeActivity : BaseActivity() {
    var totalPage = 1
    var page = 1
    lateinit var adapter: HomeTemplatesAdapter
    var homeCategoryList = ArrayList<CategoryList?>()
    var sortBy = "newest"
    val updatedList: MutableList<Any?> = mutableListOf()
    lateinit var interstitialAdsHandler: InterstitialAdsHandler
    var isQuoteClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        activity = this
        storeUserData = StoreUserData(activity)

        if (!storeUserData.getBoolean(Constants.PRIVACY_AGREEMENT)) {
            showPolicyDialog()
        }


        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        val rvCategories: RecyclerView = findViewById(R.id.rvCategories)

        swipeRefreshLayout.setOnRefreshListener {
            page = 1
            homeCategories()
        }
        rvCategories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                swipeRefreshLayout.isEnabled = newState == RecyclerView.SCROLL_STATE_IDLE
            }
        })

        tvQuotes.setOnClickListener {
            isQuoteClick = true
            showInterAdsProgress()
            if (intent.getBooleanExtra(
                    "isDownload",
                    false
                ) && storeUserData.getBoolean(Constants.PRIVACY_AGREEMENT)
            ) {
                showInterestitialAds()
            } else {
                if (::interstitialAdsHandler.isInitialized) {
                    interstitialAdsHandler.showNextAd()
                } else {
                    showInterestitialAds()
                }

            }


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

        llHomeExplore.setOnClickListener {
            startActivity(Intent(activity,ExploreActivity::class.java).putExtra("sortBy", sortBy))
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
        setupAd()

        if (!intent.getBooleanExtra(
                "isDownload",
                false
            ) && storeUserData.getBoolean(Constants.PRIVACY_AGREEMENT)
        ) {
            showInterestitialAds()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        if (::interstitialAdsHandler.isInitialized) {

            interstitialAdsHandler.onDestroy()
        }

    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        super.onPause()
        if (::interstitialAdsHandler.isInitialized) {
            interstitialAdsHandler.onDestroy()
        }
    }

    fun showInterestitialAds() {
        showInterAdsProgress()
        interstitialAdsHandler = InterstitialAdsHandler(
            this,
            getString(R.string.GL_DashbordTamplate_Inter),
            ""
        )
        interstitialAdsHandler.loadInterstitialAds()
        interstitialAdsHandler.setAdListener(object :
            InterstitialAdsHandler.InterstitialAdListeners {
            override fun onAdClosed() {
                if (isQuoteClick) {
                    activity.startActivity(Intent(activity, QuotesActivity::class.java))
                }
            }

            override fun onAdDismissed() {
                if (isQuoteClick) {
                    activity.startActivity(Intent(activity, QuotesActivity::class.java))
                }
            }

            override fun onAdLoaded() {
                dismissInterAdsProgress()
            }

            override fun onErrorAds() {
                if (isQuoteClick) {
                    dismissInterAdsProgress()

                    activity.startActivity(Intent(activity, QuotesActivity::class.java))
                }else{
                    dismissInterAdsProgress()

                }
                isQuoteClick=false

            }
        })

    }

    private fun setupAd() {
        val adRequest = AdRequest.Builder().build()
        homeBannerAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adHomeLoadingText.isVisible = false
                homeBannerAdView.isVisible = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.i("TAG", "onAdFailedToLoad: HomeScreen${loadAdError.message} ")
                Log.i("TAG", "onAdFailedToLoad: HomeScreen${loadAdError.code} ")
                adHomeLoadingText.isVisible = true
                homeBannerAdView.isVisible = false

            }
        }
        homeBannerAdView.loadAd(adRequest)
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

        if (intent.getBooleanExtra(
                "isDownload",
                false
            ) && !storeUserData.getBoolean(Constants.IS_RATE)
        ) {
            storeUserData.setBoolean(Constants.IS_RATE, true)
            rateUsDialog()
        }


    }

    private fun rateUsDialog() {
        val languageDialogClass = PartyRateDialog(this)
        languageDialogClass.setOnCancelListener(DialogInterface.OnCancelListener {

        })
        languageDialogClass.show()
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
                        if ((index + 1) % 6 == 0 && index != pojo.data.templates.size - 1) {
                            updatedList.add(NativeAdItem()) // Add a marker for the native ad
                        }
                    }
                    adapter = HomeTemplatesAdapter(
                        activity,
                        updatedList,
                        rvHomeList,
                        activity.getString(R.string.GL_DashbordTamplatelist_Native)
                    )
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
                        if ((index + 1) % 6 == 0 && index != pojo.data.templates.size - 1) {
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


    //endregion
    private fun showPolicyDialog() {
        val binding: DialogPrivacyPolicyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.dialog_privacy_policy, null, false
        )
        val dialogPolicy = Dialog(activity, R.style.PolicyDialog)
        dialogPolicy.setCancelable(false)
        dialogPolicy.setContentView(binding.getRoot())
        if (!isFinishing) dialogPolicy.show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvTerm1.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            binding.tvTerm2.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            binding.tvTerm3.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            binding.tvTerm4.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        } else {
            justifyText(binding.tvTerm1)
            justifyText(binding.tvTerm3)
            justifyText(binding.tvTerm4)
        }
        binding.layoutCloseDialog.setOnClickListener(null)
        binding.layoutMain.setOnClickListener(null)
        binding.tvDecline.setOnClickListener { v ->
            dialogPolicy.dismiss()
            super.onBackPressed()
        }
        binding.tvAgree.setOnClickListener { v ->
            storeUserData.setBoolean(Constants.PRIVACY_AGREEMENT, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissionsList: MutableList<String> = java.util.ArrayList()
                if (Build.VERSION.SDK_INT >= 33) {
                    permissionsList.add(Manifest.permission.READ_MEDIA_IMAGES)
                    permissionsList.add(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
                if (!permissionsList.isEmpty()) {
                    requestPermissions(
                        permissionsList.toTypedArray(),
                        123
                    )
                }
            }

            dialogPolicy.dismiss()
        }
    }

    private fun justifyText(textView: TextView) {
        val isJustify = AtomicBoolean(false)
        val textString = textView.text.toString()
        val textPaint = textView.paint
        val builder = SpannableStringBuilder()
        textView.post {
            if (!isJustify.get()) {
                val lineCount = textView.lineCount
                val textViewWidth = textView.width
                for (i in 0 until lineCount) {
                    val lineStart = textView.layout.getLineStart(i)
                    val lineEnd = textView.layout.getLineEnd(i)
                    val lineString = textString.substring(lineStart, lineEnd)
                    if (i == lineCount - 1) {
                        builder.append(SpannableString(lineString))
                        break
                    }
                    val trimSpaceText = lineString.trim { it <= ' ' }
                    val removeSpaceText = lineString.replace(" ".toRegex(), "")
                    val removeSpaceWidth = textPaint.measureText(removeSpaceText)
                    val spaceCount =
                        (trimSpaceText.length - removeSpaceText.length).toFloat()
                    val eachSpaceWidth = (textViewWidth - removeSpaceWidth) / spaceCount
                    val spannableString = SpannableString(lineString)
                    for (j in 0 until trimSpaceText.length) {
                        val c = trimSpaceText[j]
                        if (c == ' ') {
                            val drawable: Drawable = ColorDrawable(0x00ffffff)
                            drawable.setBounds(0, 0, eachSpaceWidth.toInt(), 0)
                            val span = ImageSpan(drawable)
                            try {
                                spannableString.setSpan(
                                    span,
                                    j,
                                    j + 1,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            } catch (e: Exception) {
                            }
                        }
                    }
                    builder.append(spannableString)
                }
                textView.text = builder
                isJustify.set(true)
            }
        }
    }


}