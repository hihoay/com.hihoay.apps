package app.module.ad_network.admob

import android.app.Activity
import android.widget.LinearLayout
import app.module.enums.AdState
import app.module.objecs.AdFormatManager
import app.module.objecs.MyAd
import app.module.utils.IS_TESTING
import app.module.utils.TaymayContext
import app.module.utils.elog
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback


object InterstitialRewardAd : AdFormatManager() {


    override fun loadAd(myAd: MyAd, viewContainer: LinearLayout) {
        val rewardedAd: RewardedInterstitialAd
        if (myAd.ad_cache != null && myAd.ad_cache is RewardedAd) {
            rewardedAd = myAd.ad_cache as RewardedInterstitialAd
            myAd.setState(rewardedAd, AdState.Loaded)
            return
        }
        RewardedInterstitialAd.load(
            TaymayContext,
            if (IS_TESTING) "ca-app-pub-3940256099942544/5354046379" else myAd.ad_id,
            AdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {

                    elog("InterstitialRewardAd was loaded.")

                    myAd.setState(rewardedInterstitialAd, AdState.Loaded)

                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    elog("InterstitialRewardAd was onAdFailedToLoad.", loadAdError.message)
                    myAd.setState(null, AdState.Error)

                }
            })
    }

    override fun showAd(myAd: MyAd, viewContainer: LinearLayout) {
        val adSave = myAd.ad_cache
        var rewardedInterstitialAd: RewardedInterstitialAd? = null
        rewardedInterstitialAd = if (adSave is RewardedInterstitialAd) {
            adSave
        } else {
            myAd.setState(null, AdState.Error)
            return
        }

        rewardedInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                //elog("Ad was clicked.")
                myAd.setState(null, AdState.AdClick)

            }

            override fun onAdDismissedFullScreenContent() {
                //elog("Ad dismissed fullscreen content.")
                myAd.setState(null, AdState.Close)

            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                myAd.setState(null, AdState.Error)

                //elog("Ad failed to show fullscreen content.")
            }


            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                myAd.setState(null, AdState.Show)

                //elog("Ad showed fullscreen content.")
            }
        }
        if (TaymayContext is Activity) rewardedInterstitialAd.show(TaymayContext as Activity) { reward ->
            run {
                myAd.setState(null, AdState.Done)
                val rewardAmount = reward.amount
                val rewardType = reward.type
            }


        } else myAd.setState(null, AdState.Error)


    }

}