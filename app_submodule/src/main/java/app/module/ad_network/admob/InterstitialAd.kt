package app.module.ad_network.admob

import android.app.Activity
import android.widget.LinearLayout
import app.module.enums.AdState
import app.module.objecs.AdFormatManager
import app.module.objecs.MyAd
import app.module.utils.IS_TESTING
import app.module.utils.TaymayApplication
import app.module.utils.TaymayContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAd : AdFormatManager() {
    override fun loadAd(myAd: MyAd, viewContainer: LinearLayout) {
        val interstitialAd: InterstitialAd?
        if (myAd.ad_cache != null && myAd.ad_cache is InterstitialAd) {
            interstitialAd = myAd.ad_cache as InterstitialAd?
            if (interstitialAd != null) {
                myAd.setState(interstitialAd, AdState.Loaded)
            }
            return
        }
        InterstitialAd.load(
            TaymayApplication,
            if (IS_TESTING) "ca-app-pub-3940256099942544/1033173712" else myAd.ad_id,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    myAd.setState(interstitialAd, AdState.Loaded)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    val error = String.format(
                        "domain: %s, code: %d, message: %s",
                        loadAdError.domain, loadAdError.code, loadAdError.message
                    )
                    //elog("onAdFailedToLoad", error)
                    myAd.setState(null, AdState.Error)
                }
            })
    }

    override fun showAd(myAd: MyAd, viewContainer: LinearLayout) {
        val interstitialAd: InterstitialAd
        val adSave = myAd.ad_cache
        interstitialAd = if (adSave is InterstitialAd) {
            adSave
        } else {
            myAd.setState(null, AdState.Error)
            return
        }
        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                //elog("-------------------->onAdDismissedFullScreenContent")
                myAd.setState(null, AdState.Close)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                //elog("-------------------->onAdFailedToShowFullScreenContent")

                myAd.setState(null, AdState.Error)
            }

            override fun onAdShowedFullScreenContent() {
                //elog("-------------------->onAdShowedFullScreenContent")
                myAd.setState(null, AdState.Show)
            }

            override fun onAdClicked() {
                //elog("-------------------->onAdClicked")

                super.onAdClicked()
            }

            override fun onAdImpression() {
                //elog("-------------------->onAdImpression")

                super.onAdImpression()
            }
        }

        if (TaymayContext is Activity) interstitialAd.show(TaymayContext as Activity)
        else {
            myAd.setState(null, AdState.Error)
        }
    }


}