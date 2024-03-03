package app.module.admob

import android.content.Context
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import app.module.enums.AdFormat
import app.module.objecs.MyAd
import app.module.utils.TaymayApplication
import app.module.utils.TaymayContext
import app.module.utils.elog
import com.google.android.gms.ads.AdSize

object AdmobManager {

    private fun getAdBannerInlineAdaptiveSize(viewContainer: ViewGroup): AdSize {
        var windowManager =
            TaymayContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels = viewContainer.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt() - 15
        return AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(
            TaymayApplication, adWidth
        )
    }

    private fun getAdBannerAdaptiveAnchorSize(): AdSize {
        var windowManager =
            TaymayContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            TaymayApplication, adWidth
        )
    }

    fun getAdSizeByTag(tag: String, viewContainer: ViewGroup): AdSize {
        try {
            try {
                if (tag.contains("320x100"))
                    return AdSize.LARGE_BANNER
                if (tag.contains("320x250"))
                    return AdSize.MEDIUM_RECTANGLE
                if (tag.contains("320x50"))
                    return AdSize.BANNER
                if (tag.contains("anchor"))
                    return getAdBannerAdaptiveAnchorSize()
                if (tag.contains("inline"))
                    return getAdBannerInlineAdaptiveSize(viewContainer)
            } catch (e: Exception) {
            }
        } catch (e: Exception) {
            elog("getAdSizeByTag", e.message!!)
        }
        return getAdBannerAdaptiveAnchorSize()

    }

    fun loadAd(myAd: MyAd, viewContainer: LinearLayout) {
        when (myAd.adFormat) {
            AdFormat.Banner -> {
                BannerAd.loadAd(myAd, viewContainer)
            }

            AdFormat.Native -> {
                NativeAd.loadAd(myAd, viewContainer)
            }

            AdFormat.OpenAd -> {
                OpenAd.loadAd(myAd, viewContainer)
            }

            AdFormat.Interstitial -> {
                InterstitialAd.loadAd(myAd, viewContainer)
            }

            AdFormat.Interstitial_Reward -> {
                InterstitialRewardAd.loadAd(myAd, viewContainer)
            }

            AdFormat.Reward -> {
                RewardAd.loadAd(myAd, viewContainer)
            }

            else -> {}
        }
    }

    fun showAd(myAd: MyAd, viewContainer: LinearLayout) {
        when (myAd.adFormat) {
            AdFormat.Banner -> {
                BannerAd.showAd(myAd, viewContainer)
            }

            AdFormat.Native -> {
                NativeAd.showAd(myAd, viewContainer)
            }

            AdFormat.OpenAd -> {
                OpenAd.showAd(myAd, viewContainer)
            }

            AdFormat.Interstitial -> {
                InterstitialAd.showAd(myAd, viewContainer)
            }

            AdFormat.Reward -> {
                RewardAd.showAd(myAd, viewContainer)
            }

            AdFormat.Interstitial_Reward -> {
                InterstitialRewardAd.showAd(myAd, viewContainer)
            }

            else -> {}
        }

    }
}