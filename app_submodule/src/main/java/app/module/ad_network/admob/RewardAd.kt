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
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object RewardAd : AdFormatManager() {

    override fun loadAd(myAd: MyAd, viewContainer: LinearLayout) {

        val rewardedAd: RewardedAd
        if (myAd.ad_cache != null && myAd.ad_cache is RewardedAd) {
            rewardedAd = myAd.ad_cache as RewardedAd
            myAd.setState(rewardedAd, AdState.Loaded)
            return
        }
        RewardedAd.load(
            TaymayApplication,
            if (IS_TESTING) "ca-app-pub-3940256099942544/5224354917" else myAd.ad_id,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    elog("RewardedAd", "onAdFailedToLoad", loadAdError.message)
                    myAd.setState(null, AdState.Error)
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    elog("RewardedAd", "onAdLoaded")

                    myAd.setState(rewardedAd, AdState.Loaded)
                }
            })
    }

    override fun showAd(myAd: MyAd, viewContainer: LinearLayout) {

        val adSave = myAd.ad_cache
        var rewardedAd: RewardedAd? = null
        rewardedAd = if (adSave is RewardedAd) {
            adSave
        } else {
            myAd.setState(null, AdState.Error)
            return
        }
        rewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                myAd.setState(null, AdState.Show)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                myAd.setState(null, AdState.Error)
            }

            override fun onAdDismissedFullScreenContent() {
                myAd.setState(null, AdState.Close)
            }

            override fun onAdClicked() {
                myAd.setState(null, AdState.AdClick)

            }
        }
        if (TaymayContext is Activity) rewardedAd.show(
            TaymayContext as Activity
        ) { rewardItem ->
            myAd.setState(null, AdState.Done)
            val rewardAmount = rewardItem.amount
            val rewardType = rewardItem.type
        }
        else {
            elog("  rewardedAd.show() is not Activity")
            myAd.setState(null, AdState.Error)

        }
    }


}