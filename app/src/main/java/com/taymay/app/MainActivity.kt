package com.taymay.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.module.activities.IntroActivity
import app.module.activities.ShowType
import app.module.lang.timerInterval
import app.module.objecs.loadAndShowAdInLayout
import app.module.objecs.loadAdsInBackgroud
import app.module.utils.taymayActivityLoadAndShowAdCallbackWaitFor
import app.module.utils.taymayActivityLoadAndShowAdCallbackFor
import app.module.utils.taymayAskExitApp
import app.module.utils.elog
import app.module.utils.taymayFirebaseEventTracking
import app.module.utils.taymayGoHomeScreen
import app.module.utils.taymayInitReturnAppAd
import app.module.utils.taymayInitUMP
import app.module.utils.taymayIsCanRequestAds
import app.module.utils.taymayIsPrivacyOptionsRequired
import app.module.utils.taymayOpenSetLangActivity
import app.module.utils.showPrivacyOptionsForm
import app.module.utils.taymayGetAdVersion
import com.taymay.chatbot.R
import com.taymay.chatbot.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    fun getDemoView(): TextView {
        var tvHello = TextView(this)
        tvHello.setText("xin chao cac ban")
        tvHello.setTextColor(Color.BLUE)
        return tvHello
    }

    lateinit var activityBinding: ActivityMainBinding

    override fun onBackPressed() {
        taymayAskExitApp(this, "banner_bottom_coll", 2000) {
            taymayGoHomeScreen()
            it.finishAffinity()
        }
    }
    override fun onResume() {
        super.onResume()
//        if (activityBinding != null)
//            showAdInView(this, "banner_bottom_coll", activityBinding.llAd3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainBinding.inflate(layoutInflater)
//        testFFmpeg()
        taymayGetAdVersion(this) {
            elog("----------------------taymayGetAdVersion")
//            IntroActivity.showIntroFromViews(
//                this,
//                ShowType.Multi,
//                "banner_inline",
//                AdOn.Bottom,
//                R.drawable.bg_tut,
//                getDemoView(),
//                getDemoView(),
//                getDemoView(),
//            ) {
//            }


            taymayInitReturnAppAd(this, "open_ad")

            activityBinding.loadAds.setOnClickListener {
                taymayFirebaseEventTracking(true, "home_tap_loadads")
//                initReturnAppAd(this, "open_ad")
                loadAdsInBackgroud("interstitial_reload")
//                showAdInView(this, "banner_inline", activityBinding.llAd1)
//                showAdInView(this, "native_medium", activityBinding.llAd3)
            }

            activityBinding.showAds.setOnClickListener {
                taymayInitReturnAppAd(this, "open_ad")
                loadAndShowAdInLayout(this, "banner_inline", activityBinding.llAd1)
                loadAndShowAdInLayout(this, "banner_anchor", activityBinding.llAd2)
                loadAndShowAdInLayout(this, "native_medium", activityBinding.llAd3)
                loadAndShowAdInLayout(this, "native_small", activityBinding.llAd4)
                taymayActivityLoadAndShowAdCallbackWaitFor(this, "interstitial_reload") { ad, ac ->
                    ac?.let {
                        timerInterval(200) {
                            MainScope().launch {
                                if (!ac.isDestroyed)
                                    ac.finish()
                            }
                            startActivity(Intent(this@MainActivity, ActivityA::class.java))
                        }
                    } ?: run {
                        startActivity(Intent(this@MainActivity, ActivityA::class.java))

                    }
                }
//                loadAds("interstitial_reload")
            }

            setContentView(activityBinding.root)
            activityBinding.tvClick.setOnClickListener {
                taymayActivityLoadAndShowAdCallbackFor(this, "interstitial_reload") { ad, ac ->
                    if (
                        ac?.isDestroyed == false
                    )
                        ac.finish()
                    taymayOpenSetLangActivity(this, "native_small", "banner_anchor") {
                        elog("openSetLangActivityo done")
                    }
                }
            }
            activityBinding.showIntro.setOnClickListener {
//            loadAndShowAd(this, "interstitial", LinearLayout(this)) {
//                elog("interstitial", "state", it.adState.name)
//            }

//            if (
//                !isAdLoaded("inter_reload")
//            )
//                loadAds("inter_reload")
//            else
//            showAdInterstitialLoaded(this, "interstitial_reload") {
//                openSetLangActivity(this, "banner_anchor", MainActivity::class.java)
//                IntroActivity.showIntroFromViews(
//                    this,
//                    ShowType.Multi,
//                    "banner_bottom_coll",
//                    AdOn.Bottom,
//                    R.drawable.bg_tut,
//                    getDemoView(),
//                    getDemoView(),
//                    getDemoView(),
//                ) {
//                }
//            }
                IntroActivity.taymayShowIntroFromViews(
                    this,
                    ShowType.Multi,
                    "native_small",
                    "banner_anchor",
                    R.drawable.bg_tut,
                    getDemoView(),
                    getDemoView(),
                    getDemoView(),
                ) {
                }
//            openMoreAppActivity(this)
            }
//        askSetupLanguage(this, "bottom_language") {
//            elog("askSetupLanguage")
//        }
//        setTextLoading("hello.txt", activityBinding.tvClick)

//        isAdLoaded("ad_name_here")
//        if (isAdVersionReady())
//            loadAndShowAdCallback(this, "", LinearLayout(this)) {
//
//            }

            activityBinding.showReward.setOnClickListener {
//            loadAndShowAdReward(this, "reward_unlock") {
//                elog(it.ad_name, it.adState)
//            }
//                askSetupLanguage(this, "native_small", "banner_anchor") {
//                    elog("askSetupLanguage")
//                }
                taymayOpenSetLangActivity(this, "", "native_small") {

                }
//                openSetLangActivity(this, "banner_anchor2") {
//                    elog("openSetLangActivibanner_anchor2ty")
//                }
//                IntroActivity.showIntroFromPages(
//                    this,
//                    ShowType.Once,
//                    "iap_remove_ad",
//                    AdOn.Bottom,
//                    R.drawable.bg_tut,
//                    IntroPager(
//                        true,
//                        app.module.R.raw.anim_store_cross,
//                        "Calibration Required",
//                        R.color.white,
//                        "Swing your mobile swiftly several times and make gesture “8” to calibrate the compass.",
//                        R.color.white
//                    ),
//                    IntroPager(
//                        false,
//                        R.drawable.im_tut_2,
//                        "Customize Your Style",
//                        R.color.white,
//                        "The diverse image collection of Compass allows you to customize it according to your preferences.",
//                        R.color.white
//                    ),
//                    IntroPager(
//                        false,
//                        R.drawable.im_tut_3,
//                        "Share Your Location",
//                        R.color.white,
//                        "Easily share your location with friends and family.",
//                        R.color.white
//                    ),
//                ){
//                   elog("-->done")
//                }
//                loadAndShowAdInterstitial(this, "open_app") {
//                }

            }

            activityBinding.initAds.setOnClickListener {
//                askNotifyPermistion(this) {
//                    if (it) notify(
//                        this,
//                        Intent("Notify"),
//                        "Sang B",
//                        "Tin nhắn sang B",
//                        R.mipmap.ic_launcher
//                    )
//                    startActivity(Intent(this, ActivityA::class.java))
//                }
//                taymayGetAdVersion(this) {}

            }

            activityBinding.request.setOnClickListener {
                taymayInitUMP(this) {  b, consentInformation ->
                    elog( "initUMP", b)

                    activityBinding.check.setOnClickListener {

                        elog(
                            "canRequestAds", taymayIsCanRequestAds(this)
                        )

                        elog(
                            "privacyOptionsRequirementStatus",
                            taymayIsPrivacyOptionsRequired(this)
                        )
                        Toast.makeText(
                            this,
                            "Có thể yêu cầu quảng cáo: ${taymayIsCanRequestAds(this)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    activityBinding.reset.setOnClickListener {
                        consentInformation.reset()
                        elog(
                            "reset"
                        )
                    }
                    activityBinding.show.setOnClickListener {
                        elog(
                            "show"

                        )
                        showPrivacyOptionsForm(ConsentActivity::class.java) { i, b ->
                        }
                    }
                }
            }
        }


    }
}