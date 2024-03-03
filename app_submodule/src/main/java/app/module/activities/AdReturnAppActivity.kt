package app.module.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import app.module.databinding.LayoutAdLoadingBinding
import app.module.enums.AdState
import app.module.objecs.MyAd
import app.module.objecs.loadAdsInBackgroud
import app.module.objecs.showAd
import app.module.utils.AD_NAME
import app.module.utils.taymayFirebaseScreenTracking
import app.module.utils.taymaySetCanShowAd

class AdReturnAppActivity : Activity() {
    override fun onResume() {
        taymayFirebaseScreenTracking("return_app_view", "ReturnAppActivty")
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

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taymaySetCanShowAd(false)
        bidding = LayoutAdLoadingBinding.inflate(layoutInflater)
        setContentView(
            bidding.root
        )
        bidding.llLoadAd.visibility = View.VISIBLE
        bidding.llBackApp.visibility = View.GONE

        var ad_name = intent.getStringExtra(AD_NAME)
        if (ad_name.isNullOrBlank()) if (!isDestroyed)
            finish()
        showAd(this, ad_name.toString(), LinearLayout(this)) { myAd: MyAd ->
            when (myAd.adState) {
//                AdState.Show -> {
//                    showContinuceApp()
//                }

                AdState.Done, AdState.Error, AdState.Timeout, AdState.Close -> {
                    showContinuceApp()
                    loadAdsInBackgroud(ad_name.toString())
                    taymaySetCanShowAd(true)
                    if (!isDestroyed)
                        finish()
                }

                else -> {}
            }
        }


    }


}