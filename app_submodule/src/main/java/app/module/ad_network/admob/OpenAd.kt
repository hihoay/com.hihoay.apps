package app.module.ad_network.admob

import android.app.Activity
import android.widget.LinearLayout
import app.module.enums.AdState
import app.module.objecs.AdFormatManager
import app.module.objecs.MyAd
import app.module.utils.IS_TESTING
import app.module.utils.TaymayApplication
import app.module.utils.TaymayContext
import app.module.utils.elog
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

object OpenAd : AdFormatManager() {
    override fun loadAd(myAd: MyAd, viewContainer: LinearLayout) {

        var openAd: AppOpenAd? = null
        if (myAd.ad_cache != null && myAd.ad_cache is AppOpenAd) {
            openAd = myAd.ad_cache as AppOpenAd?
            if (openAd != null) {
                myAd.setState(openAd, AdState.Loaded)
            }
            return
        }
        val loadCallback: AppOpenAdLoadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                myAd.setState(ad, AdState.Loaded)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                elog("OpenAd", "onAdFailedToLoad", loadAdError.message)
                myAd.setState(null, AdState.Error)
            }
        }
        AppOpenAd.load(
            TaymayApplication,
            if (IS_TESTING) "ca-app-pub-3940256099942544/9257395921" else myAd.ad_id,
            AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            loadCallback
        )

    }

    override fun showAd(myAd: MyAd, viewContainer: LinearLayout) {

        val openAd: AppOpenAd
        val adSave = myAd.ad_cache
        openAd = if (adSave is AppOpenAd) {
            adSave
        } else {
            myAd.setState(null, AdState.Error)
            return
        }
        val fullScreenContentCallback: FullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    myAd.setState(null, AdState.Close)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    myAd.setState(null, AdState.Error)
                }

                override fun onAdShowedFullScreenContent() {
                    myAd.setState(null, AdState.Show)
                }
            }
        openAd.fullScreenContentCallback = fullScreenContentCallback


        if (TaymayContext is Activity) openAd.show(TaymayContext as Activity)
        else elog("openAd.show(CurrentContext as Activity) is not Activity");

    }


}