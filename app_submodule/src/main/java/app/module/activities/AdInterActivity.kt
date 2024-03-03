package app.module.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import app.module.databinding.LayoutAdLoadingBinding
import app.module.enums.AdState
import app.module.lang.timerInterval
import app.module.objecs.MyAd
import app.module.objecs.loadAdsInBackgroud
import app.module.utils.AD_NAME
import app.module.utils.taymayFirebaseScreenTracking
import app.module.utils.taymayLoadAdShowCallback
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AdInterActivity : Activity() {
    var adName: String = ""
    override fun onResume() {
        taymayFirebaseScreenTracking("ad_inter_activity_view", "AdInterActivity")
        super.onResume()
    }

    lateinit var bidding: LayoutAdLoadingBinding
    private fun showContinuceApp() {
        try {
            bidding.llLoadAd.visibility = View.GONE
            bidding.llBackApp.visibility = View.VISIBLE
            bidding.btnContinue.setOnClickListener { finish() }

        } catch (e: Exception) {

        }
    }

    companion object {
        var onAdShowDone: (myAd: MyAd, adInterActivity: AdInterActivity) -> Unit =
            fun(myAd: MyAd, adInterActivity: AdInterActivity) {
            }
        var timeWait: Long = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adName = intent.getStringExtra(AD_NAME).toString()
        bidding = LayoutAdLoadingBinding.inflate(layoutInflater)
//        bidding.tvVersionName.text = "Build ${getAppVersionName()}"
        setContentView(
            bidding.root
        )
        bidding.llLoadAd.visibility = View.VISIBLE
        bidding.llBackApp.visibility = View.GONE
        bidding.btnContinue.setOnClickListener {
            if (!isDestroyed) finish()
        }
        timerInterval(timeWait) {
            MainScope().launch {
                taymayLoadAdShowCallback(
                    this@AdInterActivity,
                    adName,
                    LinearLayout(this@AdInterActivity)
                ) { myAd ->
                    when (myAd.adState) {
                        AdState.Show -> {
                        }

                        AdState.Timeout, AdState.Error, AdState.Close, AdState.Done -> {
                            showContinuceApp()
                            if (myAd.adState == AdState.Close && myAd.isAdReload()) loadAdsInBackgroud(myAd.ad_name)
                            onAdShowDone(myAd, this@AdInterActivity)

                        }

                        else -> {}
                    }
                }
            }
        }

    }


    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}