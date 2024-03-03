package app.module.admob

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import app.module.enums.AdState
import app.module.objecs.AdFormatManager
import app.module.objecs.MyAd
import app.module.utils.IS_TESTING
import app.module.utils.TaymayApplication
import app.module.utils.elog
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError


object BannerAd : AdFormatManager() {


    override fun loadAd(myAd: MyAd, viewContainer: LinearLayout) {


        val adCached: AdView?
        if (myAd.ad_cache != null && myAd.ad_cache is AdView) {
            adCached = myAd.ad_cache as AdView?
            if (adCached != null) {
                myAd.setState(adCached, AdState.Loaded)
            }
            return
        }

        val adBanner = AdView(TaymayApplication)
        if (IS_TESTING) adBanner.adUnitId =
            "ca-app-pub-3940256099942544/6300978111" else adBanner.adUnitId =
            myAd.ad_id
        try {
            if (myAd.ad_tag.isNullOrEmpty()) myAd.ad_tag = "";
            var adRequestBuilderHeader =
                AdRequest.Builder()
            var adSize = AdmobManager.getAdSizeByTag(myAd.ad_tag, viewContainer)
            adBanner.setAdSize(adSize)


            if (myAd.ad_tag.contains("top")) {
                elog("collapsible", "top")
                val extras = Bundle()
                extras.putString("collapsible", "top")
                adRequestBuilderHeader.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            }


            if (myAd.ad_tag.contains("bottom")) {
                val extras = Bundle()
                elog("collapsible", "bottom")

                extras.putString("collapsible", "bottom")
                adRequestBuilderHeader.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            }


            adBanner.adListener = object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    myAd.setState(null, AdState.Error)
                    viewContainer.removeAllViews()
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    myAd.setState(null, AdState.Show)
                }

                override fun onAdClicked() {
                    myAd.setState(null, AdState.AdClick)
                    super.onAdClicked()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    myAd.setState(null, AdState.Close)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    myAd.setState(adBanner, AdState.Loaded)
                }
            }
            adBanner.loadAd(adRequestBuilderHeader.build())
        } catch (e: Exception) {
            elog(" BannerAd : AdFormatManager Exception", e.message!!)
            myAd.setState(null, AdState.Error)
            viewContainer.removeAllViews()


        }
    }

    override fun showAd(myAd: MyAd, viewContainer: LinearLayout) {
        var adBanner: AdView? = null
        val adSave = myAd.ad_cache
        adBanner = if (adSave is AdView) {
            adSave as AdView?
        } else {
            myAd.setState(null, AdState.Error)
            viewContainer.removeAllViews()
            return
        }
        try {
            val finalAdBanner = adBanner
            adBanner!!.adListener = object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    myAd.setState(null, AdState.Error)
                    viewContainer.removeAllViews()
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    myAd.setState(null, AdState.Show)

                }

                override fun onAdClicked() {
                    myAd.setState(null, AdState.AdClick)
                    super.onAdClicked()
                }
                override fun onAdClosed() {
                    super.onAdClosed()
                    myAd.setState(null, AdState.Close)
                }
                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    myAd.setState(finalAdBanner, AdState.Loaded)

                }
            }
            if (adBanner.parent != null) {
                (adBanner.parent as ViewGroup).removeView(adBanner)
            }
            viewContainer.removeAllViews()
            viewContainer.addView(adBanner)
            myAd.setState(null, AdState.Show)

            return
        } catch (e: Exception) {
            elog("showBannerLoaded", e.message!!)
        }

        myAd.setState(null, AdState.Error)
        viewContainer.removeAllViews()


    }

}