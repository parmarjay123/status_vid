package com.example.boozzapp.utils


import android.accounts.NetworkErrorException
import android.content.Context
import android.net.ParseException
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class RetrofitHelper {

    private var gsonAPI: API
    private var connectionCallBack: ConnectionCallBack? = null

    constructor(context: Context) {

        val httpClient = UnsafeOkHttpClient.unsafeOkHttpClient.newBuilder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                .header("Accept", "application/json")
                .header(
                    "Authorization",
                    "Bearer " + StoreUserData(context).getString(Constants.USER_TOKEN)
                )
                .method(original.method, original.body)
                .build()

            chain.proceed(request)
        }

        val TIMEOUT = 2 * 60 * 1000
        val gsonretrofit = Retrofit.Builder()
            .baseUrl(Constants.URL)
            .client(
                httpClient
                    .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS).build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        gsonAPI = gsonretrofit.create(API::class.java)


    }

    fun api(): API {
        return gsonAPI
    }


    fun callApi(
        activity: Context,
        call: Call<ResponseBody>,
        callBack: ConnectionCallBack
    ) {
        if (!Utils.isOnline(activity)) {
            Utils.internetAlert(activity)
            return
        }
        connectionCallBack = callBack
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                //Log.i("url", call.request().url().toString())
                if (response.code() == 200) {
                    if (connectionCallBack != null)
                        connectionCallBack!!.onSuccess(response)
                } else {
                    try {
                        val res = response.errorBody()!!.string()
                        if (connectionCallBack != null)
                            connectionCallBack!!.onError(response.code(), res)
                    } catch (e: IOException) {
                        //Log.i("TAG", "onResponse: " + call.request().url())
                        e.printStackTrace()
                        if (connectionCallBack != null)
                            connectionCallBack!!.onError(response.code(), e.message + "")
                    } catch (e: NullPointerException) {
                        //Log.i("TAG", "onResponse: " + call.request().url())
                        e.printStackTrace()
                        if (connectionCallBack != null)
                            connectionCallBack!!.onError(response.code(), e.message + "")
                    }

                }
            }

            override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                var message = ""
                Log.d("Retrifit",error.message!!)
                if (error is NetworkErrorException) {
                    message = "Please check your internet connection"
                } else if (error is ParseException) {
                    message = "Parsing error! Please try again after some time!!"
                } else if (error is TimeoutException) {
                    message = "Connection TimeOut! Please check your internet connection."
                } else if (error is UnknownHostException) {
                    message = "Please check your internet connection and try later"
                } else if (error is Exception) {
                    message = error.message + ""
                }
                if (connectionCallBack != null)
                    connectionCallBack!!.onError(-1, message)
            }
        })
    }

    interface ConnectionCallBack {
        fun onSuccess(body: Response<ResponseBody>)

        fun onError(code: Int, error: String)
    }

    companion object {
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        fun createBodyFromJson(descriptionString: JSONObject): RequestBody {
            return RequestBody.create(
                JSON, descriptionString.toString()
            )
        }

        fun createJSONFromString(descriptionString: JSONObject): RequestBody {
            return RequestBody.create(
                JSON, descriptionString.toString()
            )
        }

        fun prepareFilePart(partName: String, filePath: String): MultipartBody.Part {
            val requestFile = RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                File(filePath)
            )
            return MultipartBody.Part.createFormData(partName, File(filePath).name, requestFile)
        }

        fun prepareFilePartPdf(partName: String, filePath: String): MultipartBody.Part {
            val requestFile = RequestBody.create(
                "application/*".toMediaTypeOrNull(),
                File(filePath)
            )
            return MultipartBody.Part.createFormData(partName, File(filePath).name, requestFile)
        }

        fun prepareFilePartText(partName: String, filePath: String): MultipartBody.Part {
            val requestFile = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                File(filePath)
            )
            return MultipartBody.Part.createFormData(partName, File(filePath).name, requestFile)
        }

        fun createPartFromString(descriptionString: String): RequestBody {
            return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString
            )
        }
    }
}

/******************USAGE**********************
 *
private fun login() {
showProgress()
val retrofitHelper = RetrofitHelper()
var call: Call<ResponseBody> =
retrofitHelper.api().processLogin(
email.text.toString(),
password.text.toString(),
"android",
storeUserData.getString(Constants.USER_FCM)
)
retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
override fun onSuccess(body: Response<ResponseBody>) {
dismissProgress()
if (body.code() != 200) {
return
}
val responseString = body.body()!!.string()
Log.i("TAG", responseString)
var json = JSONObject(responseString)
var reader = StringReader(responseString);
var gson = GsonBuilder().create()
var liveMatch = gson.fromJson(reader, LiveMatch.class)
}

override fun onError(code: Int, error: String) {

}
})
}
 */