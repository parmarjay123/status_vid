package com.example.boozzapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.boozzapp.R
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class SplashActivity : BaseActivity() {
    private var isActivityFinishing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        activity = this
        storeUserData = StoreUserData(activity)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(activity) { instanceIdResult ->
            val newToken = instanceIdResult.token
            Log.e("newToken", newToken)
            addDeviceToken(newToken)
            storeUserData.setString(Constants.USER_FCM, newToken)

        }

        if (intent.data != null) {
            val uri = Uri.parse(intent.data?.toString())
            if (uri.host == "template" && uri.getQueryParameter("id") != null) {
                startActivity(
                    Intent(activity, PreviewActivity::class.java).putExtra(
                        "videoId",
                        uri.getQueryParameter("id")
                    )
                )
                finish()
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                if (!isActivityFinishing) {
                    startActivity(Intent(activity, HomeActivity::class.java))
                    finish()
                }
            }, 1500)
        }

    }

    override fun onDestroy() {
        isActivityFinishing = true
        super.onDestroy()
    }


    private fun addDeviceToken(token: String) {
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().addDeviceToken(
                token, ""
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                val responseString = body.body()!!.string()
                Log.i("TAG", "homeTemplateList$responseString")

            }

            override fun onError(code: Int, error: String) {
               // dismissProgress()
                Log.i("error", error)

            }
        })
    }

}
