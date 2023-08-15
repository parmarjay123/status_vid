import android.content.Context
import com.facebook.ads.NativeAd
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd as GoogleNativeAd

class NativeAdItem {
    var nativeAd: GoogleNativeAd? = null

    fun loadAds(context: Context, adUnitId: String) {
        // Load Google native ad
        val googleAdLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                }
            })
            .build()
        googleAdLoader.loadAd(AdRequest.Builder().build())
    }


}
