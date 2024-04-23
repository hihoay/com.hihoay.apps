package app.module.ad_network.admob

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import app.module.enums.AdState
import app.module.objecs.AdFormatManager
import app.module.objecs.MyAd
import app.module.objecs.NativeTemplateStyle
import app.module.objecs.TemplateView
import app.module.utils.IS_TESTING
import app.module.utils.TaymayApplication
import app.module.utils.elog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

object NativeAd : AdFormatManager() {
    override fun loadAd(myAd: MyAd, viewContainer: LinearLayout) {


        val adCached: NativeAd?
        if (myAd.ad_cache != null && myAd.ad_cache is NativeAd) {
            adCached = myAd.ad_cache as NativeAd?
            if (adCached != null) {
                myAd.setState(adCached, AdState.Loaded)
            }
            return
        }


        val builder = AdLoader.Builder(
            TaymayApplication,
            if (IS_TESTING) "ca-app-pub-3940256099942544/2247696110" else myAd.ad_id
        )
            .forNativeAd { nativeAd ->
                myAd.setState(nativeAd, AdState.Loaded)
            }
        val adLoader = builder
            .withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        elog("native-----------------------onAdFailedToLoad--${loadAdError.message}")
                        myAd.setState(null, AdState.Error)
                        viewContainer.removeAllViews()

                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        myAd.setState(null, AdState.AdClick)
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        myAd.setState(null, AdState.Close)
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        myAd.setState(null, AdState.Show)
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                    }
                })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun showAd(myAd: MyAd, viewContainer: LinearLayout) {

        var nativeAd: NativeAd? = null
        val adSave = myAd.ad_cache
        if (adSave is NativeAd) {
            try {
                nativeAd = adSave
                val nativeTemplateStyle = NativeTemplateStyle.Builder().build()
                var view =
                    View.inflate(
                        TaymayApplication,
                        app.module.R.layout.temp_native_ad_medium,
                        null
                    )
                if (myAd.ad_tag == "small") view =
                    View.inflate(
                        TaymayApplication,
                        app.module.R.layout.temp_native_ad_small,
                        null
                    )
                val templateView =
                    view.findViewById<TemplateView>(app.module.R.id.temp_view_native)
                templateView.setStyles(nativeTemplateStyle)
                templateView.setNativeAd(nativeAd)
                if (view.parent != null) {
                    (view.parent as ViewGroup).removeView(view)
                }
                viewContainer.removeAllViews()
                viewContainer.addView(view)
                myAd.setState(null, AdState.Show)
            } catch (e: Exception) {
                myAd.setState(null, AdState.Error)
                viewContainer.removeAllViews()
            }
            return
        } else {
            myAd.setState(null, AdState.Error)
            viewContainer.removeAllViews()
            return
        }
    }


}