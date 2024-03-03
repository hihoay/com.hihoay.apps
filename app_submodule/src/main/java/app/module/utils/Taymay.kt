package app.module.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import app.module.R
import app.module.activities.*
import app.module.activities.MoreAppActivity.Companion.initMoreAppData
import app.module.enums.AdFormat
import app.module.enums.AdNet
import app.module.enums.AdState
import app.module.lang.getData
import app.module.lang.timerInterval
import app.module.objecs.*
import app.module.views.getLines
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.limurse.iap.DataWrappers
import com.limurse.iap.IapConnector
import com.limurse.iap.PurchaseServiceListener
import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


object Config {
    var anim_splash: Int = R.raw.anim_loading_cross
}


@SuppressLint("StaticFieldLeak")
lateinit var TaymayContext: Context
lateinit var TaymayApplication: Application
private lateinit var consentInformation: ConsentInformation
private var isMobileAdsInitializeCalled = AtomicBoolean(false)

fun taymayGetAdVersion(context: Context, readyToUse: () -> Unit) {

    TaymayContext = context

    taymayInitAds()
    initDataVersion(TaymayContext) {
    }

    getAdsRemote {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            readyToUse()
        }
    }

//    try {
//        (context as Activity).initUMP { b, consentInformation -> }
//    } catch (e: Exception) {
//    }

}

fun Activity.showPrivacyOptionsForm(
    clHome: Class<*>,
    callback: (Int, String) -> Unit
) {
    UserMessagingPlatform.showPrivacyOptionsForm(this) {
        elog("showPrivacyOptionsForm")
        it?.let { it1 -> callback(it1.errorCode, it.message) }
        var intent = Intent(this, clHome)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}


fun taymayIsCanRequestAds(context: Context): Boolean {
    return getConsentInformation(context).canRequestAds()
}

/** Helper variable to determine if the privacy options form is required. */

fun taymayIsPrivacyOptionsRequired(context: Context): Boolean {
    return getConsentInformation(context).privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
}

fun getConsentInformation(context: Context): ConsentInformation {
    if (!::consentInformation.isInitialized)
        consentInformation = UserMessagingPlatform.getConsentInformation(context)

    return consentInformation
}

fun taymayInitUMP(context: Activity, onInited: (Boolean, ConsentInformation) -> Unit) {

    isMobileAdsInitializeCalled.set(false)
    var consentInformation = getConsentInformation(context)
    val debugSettings = ConsentDebugSettings.Builder(context)
        .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
        .addTestDeviceHashedId(HASH_UMP_TEST)
        .build()
    var params =
        if (IS_TESTING)
            ConsentRequestParameters
                .Builder()
                .setConsentDebugSettings(debugSettings)
                .build()
        else ConsentRequestParameters
            .Builder()
            .build()

    consentInformation.requestConsentInfoUpdate(
        context,
        params,
        {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                context,
                ConsentForm.OnConsentFormDismissedListener { loadAndShowError ->
                    loadAndShowError?.let {
                        String.format(
                            "%s: %s",
                            it.errorCode,
                            loadAndShowError.message
                        )
                    }
                    if (!isMobileAdsInitializeCalled.getAndSet(true)) {
                        elog("OnConsentFormDismissedListener", taymayIsCanRequestAds(context))
                        onInited(taymayIsCanRequestAds(context), consentInformation)
                    }
                }
            )
        },
        { requestConsentError ->
            elog(
                String.format(
                    "%s: %s",
                    requestConsentError.errorCode,
                    requestConsentError.message
                )
            )
            if (!isMobileAdsInitializeCalled.getAndSet(true)) {
                elog("requestConsentError", taymayIsCanRequestAds(context))
                onInited(taymayIsCanRequestAds(context), consentInformation)
            }
        })

    if (!MyConnection.taymayIsOnline(context))
        if (!isMobileAdsInitializeCalled.getAndSet(true)) {
            elog("!taymayIsOnline", taymayIsCanRequestAds(context))
            onInited(taymayIsCanRequestAds(context), consentInformation)
        }

    if (taymayIsCanRequestAds(context))
        if (!isMobileAdsInitializeCalled.getAndSet(true)) {
            elog("taymayIsCanRequestAds", taymayIsCanRequestAds(context))
            onInited(taymayIsCanRequestAds(context), consentInformation)
        }
}

fun taymaySetCanShowAd(boolean: Boolean) {
    IsCanShowAd = boolean
}

fun taymayDownloadFileFromUrl(
    urlStr: String,
    file: String,
    timout: Int = 12000,
    callback: (b: Boolean) -> Unit
) {
    GlobalScope.launch {
        try {
            org.apache.commons.io.FileUtils.copyURLToFile(
                URL(urlStr),
                File(file),
                timout,
                timout
            )
            MainScope().launch {
                callback(true)
            }
            return@launch
        } catch (e: Exception) {
            elog(e.message!!)
        }
        MainScope().launch {
            callback(false)
        }

    }
}


fun taymaySetupApplication(application: Application, idProducts: String) {
    application.setCount(KEY_TRACKING, 1)
    var upCount = application.upCount("open_time", 1, 0).toLong()
    elog("upCount", upCount)
    taymayGetGeoIP {
        application.taymayLogData("open_app_time", long_value = upCount, string_value = it.jsonOriginal)
    }
    TaymayContext = application
    TaymayApplication = application
    PRODUCTS = idProducts
    if (!IS_TESTING) {
        AD_CONFIG_VERSION_DEFAULT = "default"
        DATA_CONFIG_VERSION_DEFAULT = "default"
    }
//    initTaymayAds()
}

private fun taymayInitAds() {
    var listNonCons: List<String>
    var listCons: List<String>
    var listSubs: List<String>
    listNonCons =
        taymayGetDataString("iap", PRODUCTS).split(',').map { it.trim() }
    listCons = Arrays.asList()
    listSubs = Arrays.asList()
    MobileAds.initialize(TaymayApplication) { initializationStatus ->
        val statusMap =
            initializationStatus.adapterStatusMap
        for (adapterClass in statusMap.keys) {
            val status = statusMap[adapterClass]
            elog(
                "MyApp", String.format(
                    "Adapter name: %s, Description: %s, Latency: %d",
                    adapterClass, status!!.description, status.latency
                )
            )
        }
    }

    initMoreAppData(TaymayApplication) {}
    //------------------
    val iapConnector: IapConnector =
        IapConnector(TaymayApplication, listNonCons, listCons, listSubs)
    val purchaseServiceListener: PurchaseServiceListener = object : PurchaseServiceListener {
        override fun onPricesUpdated(iapKeyPrices: Map<String, List<DataWrappers.ProductDetails>>) {
        }

        override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
            TaymayApplication.taymayLogString("onProductPurchased-loader", purchaseInfo.originalJson)
            MyCache.putBooleanValueByName(
                TaymayApplication,
                IS_PREMIUM,
                true
            )
        }

        override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
            TaymayApplication.taymayLogString("onProductRestored-loader", purchaseInfo.originalJson)
            if (purchaseInfo.sku in listNonCons) MyCache.putBooleanValueByName(
                TaymayApplication,
                IS_PREMIUM,
                true
            )
        }

        override fun onPurchaseFailed(
            purchaseInfo: DataWrappers.PurchaseInfo?,
            billingResponseCode: Int?
        ) {

        }
    }
    iapConnector.addPurchaseListener(purchaseServiceListener)
}

fun taymayGetAppVersionCode(context: Context): String {
    try {
        TaymayContext = context

        val pInfo: PackageInfo =
            context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
        val version: String = pInfo.versionCode.toString()
        return version
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return ""
}

fun taymayGetAppVersionName(context: Context): String {
    TaymayContext = context

    try {
        val pInfo: PackageInfo =
            context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
        val version: String = pInfo.versionName
        return version
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return ""
}

fun Context.taymayFirebaseScreenTracking(screen_name: String, screen_class: String) {
    val firebaseAnalytics = FirebaseAnalytics.getInstance(TaymayContext)
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundleOf().apply {
        putString(FirebaseAnalytics.Param.SCREEN_NAME, screen_name)
        putString(FirebaseAnalytics.Param.SCREEN_CLASS, screen_class)
    })

}

fun Context.taymayFirebaseEventTracking(
    isCount: Boolean,
    event: String,
    vararg pair: Pair<String, Any>
) {
    FirebaseAnalytics.getInstance(TaymayContext)
        .logEvent(event, bundleOf(*pair))
    taymayEventTracking(isCount, event)

}

fun taymayOpenSetLangActivity(
    context: Context,
    adTopLanguageName: String,
    adBottomLanguageName: String,
    function: () -> Unit
) {
    TaymayContext = context
    SetupLanguageActivity.adTopLanguageName = adTopLanguageName
    SetupLanguageActivity.adBottomLanguageName = adBottomLanguageName
    SetupLanguageActivity.callbackSetupLanguage = function
    TaymayContext.startActivity(Intent(TaymayContext, SetupLanguageActivity::class.java))
}

fun taymayIsWillShowLanguageSetup(context: Activity): Boolean {
    var x = getData(context, SetupLanguageActivity.LANGUAGE_PREF);
    if (x == "")
        return true
    return false
}

fun Activity.taymayClearStackAndGoActivity(calActivity: Class<*>) {
    val intent = Intent(this, calActivity)
    startActivity(intent.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    })
}

fun Activity.taymayGoHomeScreen() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    startActivity(intent.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    })

    finishAffinity()
}

fun taymayAskFirstSetupLanguage(
    context: Context,
    adTopLanguageName: String,
    adBottomLanguageName: String,
    function: () -> Unit
) {
    TaymayContext = context
    SetupLanguageActivity.adTopLanguageName = adTopLanguageName
    SetupLanguageActivity.adBottomLanguageName = adBottomLanguageName
    SetupLanguageActivity.check(TaymayContext as Activity, function)
}

fun taymayAskExitApp(
    context: Context,
    idAd: String, waitToExit: Long,
    runExitApp: (exitActivity: ExitAppActivity) -> Unit
) {
    TaymayContext = context
    ExitAppActivity.runExitApp = runExitApp
    ExitAppActivity.waitToExit = waitToExit
    if (TaymayContext is Activity) ExitAppActivity.askExitApp(idAd, TaymayContext as Activity)
    else elog("askExitApp: context not is Activity")

}


fun taymayOpenMoreAppActivity(context: Context) {
    MoreAppActivity.Open(TaymayContext as Activity); // tham số đầu vào là  một activity hiện tại
}

fun taymayGetRemoteData(
    key: String, callback: (content: String) -> Unit
) {
    val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    val configSettings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(if (IS_TESTING) 0 else 3600).build()
    mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
        var value = mFirebaseRemoteConfig.getString(key)
        elog("getRemoteData", key, value)
        callback(value)
    }
}

fun taymayIsOneAdOpenFullScreen(): Boolean {
    var b: Boolean = false
    try {
        var adShowing =
            adsConfig.filter { it.adFormat == AdFormat.Interstitial || it.adFormat == AdFormat.OpenAd }
                .filter { it.adState == AdState.Show }.firstOrNull()
        if (adShowing != null) b = true
    } catch (e: Exception) {
    }
    return b


}

fun taymayIsCanShowOrLoadNow(context: Context, adName: String): Boolean {
    TaymayContext = context
    var myAd = getAdByAdName(adName)
    if (!myAd.isCanShow(context)) return false

    elog("myAd.isCanShow(context)", myAd.isCanShow(context))
    if (!IsCanShowAd) return false
    val last = (System.currentTimeMillis() - MyCache.getLongValueByName(
        TaymayContext, AdShowLastTime, 0
    )) / 1000

    if (taymayIsOneAdOpenFullScreen()) return false
    var display_distance = myAd.ad_distance
    var isCanShow = last >= display_distance
    pushNotify("Ad is Can Show", isCanShow, "$last/${display_distance}")
    return isCanShow
}


fun taymayDialogLoadAndShowReward(
    context: Context,
    adName: String, callback: (myAd: MyAd) -> Unit
) {
    TaymayContext = context

    showDialogLoadAd(context, adName) { isCanShow, myAd ->
        if (isCanShow) {
            if (getAdByAdName(adName)
                    ?.isAdLoaded() == true
            ) {
                loadAd(
                    adName, LinearLayout(TaymayContext)
                ) { isCanShow, myAd ->
                    if (isCanShow) {
                        showAd(context, adName, LinearLayout(TaymayContext)) {
                            callback(it)
                        }
                    } else callback(myAd)
                }
            } else callback(MyAd(adName))
        } else callback(MyAd(adName))
    }
}


fun taymayShowAdInterstitialLoaded(
    context: Context,
    adName: String, runAffterShow: () -> Unit
) {
    TaymayContext = context

    if (!taymayIsCanShowOrLoadNow(context, adName)) {
        runAffterShow()
        return
    }
    if (getAdByAdName(adName).isAdLoaded()) {
        loadAd(
            adName, LinearLayout(TaymayContext)
        ) { isCanShow, myAd ->
            if (isCanShow) {
                showAd(context, adName, LinearLayout(TaymayContext)) {
                    when (it.adState) {
                        AdState.Close, AdState.Timeout, AdState.Error, AdState.Done -> {
                            if (myAd.isAdReload() && myAd.adState == AdState.Close) loadAdsInBackgroud(
                                adName
                            )
                            runAffterShow()
                        }

                        else -> {}
                    }
                }
            } else {
                if (myAd.isAdReload()) loadAdsInBackgroud(adName)
                runAffterShow()
            }
        }
    } else {
        runAffterShow()

    }
}

fun taymayDialogLoadAndShowAdInterstitial(
    context: Context,
    adName: String, runAfterShow: () -> Unit
) {
    TaymayContext = context

    if (!taymayIsCanShowOrLoadNow(
            context,
            adName
        ) || !MyConnection.taymayIsOnline(context) || !isHasAd(adName)
    ) {
        runAfterShow()
        return
    }
    showDialogLoadAd(context, adName) { isCanShow, myAd ->
        if (isCanShow) {
            taymayShowAdInterstitialLoaded(context, adName) {
                runAfterShow()
            }
        } else runAfterShow()
    }

}

fun taymayLoadAndShowAdCloseActivity(
    context: Context,
    adName: String,
    runAfterShow: (myAd: MyAd?, adInterActivity: AdInterActivity?) -> Unit
) {
    TaymayContext = context

    if (!taymayIsCanShowOrLoadNow(
            context,
            adName
        ) || !MyConnection.taymayIsOnline(context) || !isHasAd(adName)
    ) {
        runAfterShow(null, null)
        (context as Activity).finish()
        return
    }
    AdInterActivity.onAdShowDone = runAfterShow
    AdInterActivity.timeWait = 500
    var intent = Intent(TaymayContext, AdInterActivity::class.java)
    intent.putExtra(AD_NAME, adName)
    TaymayContext.startActivity(intent)
    (context as Activity).finish()
}

fun taymayShowAdLoadedToOpenActivity(
    context: Context,
    adName: String, runAfterShow: () -> Unit
) {
    TaymayContext = context
    if (!isAdLoaded(adName)) {
        try {
            if (
                getAdByAdName(adName).isAdReload()
            ) loadAdsInBackgroud(adName)

        } catch (e: Exception) {
        }
        runAfterShow()
    } else taymayShowAdInterstitialLoaded(context, adName, runAfterShow)
}


fun taymayActivityLoadAndShowAdCachedCallbackWaitFor(
    context: Context,
    adName: String,
    runAfterShow: (myAd: MyAd?, adInterActivity: AdInterActivity?) -> Unit
) {
    TaymayContext = context
    if (!isAdLoaded(adName)) {
        try {
            if (getAdByAdName(adName).isAdReload()) loadAdsInBackgroud(adName)
        } catch (_: Exception) {
        }
        runAfterShow(null, null)
        return
    }
    if (!taymayIsCanShowOrLoadNow(
            context,
            adName
        ) || !MyConnection.taymayIsOnline(context) || !isHasAd(adName)
    ) {
        runAfterShow(null, null)
        return
    }
    AdInterActivity.onAdShowDone = runAfterShow
    AdInterActivity.timeWait = taymayGetDataDouble("ad_wait_to_show", 200.0).toLong()
    var intent = Intent(TaymayContext, AdInterActivity::class.java)
    intent.putExtra(AD_NAME, adName)
    TaymayContext.startActivity(intent)
}

fun Context.callbackCloseActivityLoadingCallbackFor(
    ad: MyAd?,
    ac: AdInterActivity?,
    function: (ad: MyAd?, ac: AdInterActivity?) -> Unit
) {
    ac?.let {
        timerInterval(taymayGetDataLong("ad_wait_to_done", 1)) {
            MainScope().launch {
                if (!ac.isDestroyed) ac.finish()
                function(ad, ac)
            }
        }
    } ?: run {
        function(ad, ac)
    }
}

fun Context.taymayActivityLoadAndShowAdCachedCallbackFor(
    context: Context, adName: String, function: (ad: MyAd?, ac: AdInterActivity?) -> Unit
) {
    taymayActivityLoadAndShowAdCachedCallbackWaitFor(context, adName) { ad, ac ->
        callbackCloseActivityLoadingCallbackFor(ad, ac, function)
    }
}

fun Context.taymayActivityLoadAndShowAdCallbackFor(
    context: Context, adName: String, function: (ad: MyAd?, ac: AdInterActivity?) -> Unit
) {
    taymayActivityLoadAndShowAdCallbackWaitFor(context, adName) { ad, ac ->
        callbackCloseActivityLoadingCallbackFor(ad, ac, function)
    }
}

fun taymayActivityLoadAndShowAdCallbackWaitFor(
    context: Context,
    adName: String,
    runAfterShow: (myAd: MyAd?, adInterActivity: AdInterActivity?) -> Unit
) {
    TaymayContext = context

    if (!taymayIsCanShowOrLoadNow(
            context,
            adName
        ) || !MyConnection.taymayIsOnline(context) || !isHasAd(adName)
    ) {
        runAfterShow(null, null)
        return
    }
    AdInterActivity.onAdShowDone = runAfterShow
    AdInterActivity.timeWait = 0
    var intent = Intent(TaymayContext, AdInterActivity::class.java)
    intent.putExtra(AD_NAME, adName)
    TaymayContext.startActivity(intent)
}


fun taymayAskRateAndFeedbackNextSession(context: Context, onDone: () -> Unit) {
    TaymayContext = context
    DialogRateAndReview().askRateAndFeedbackNextSession(TaymayContext, onDone)
}

fun taymayShowDialogCheckAppOnStore(context: Context) {
    TaymayContext = context

    val builder = AlertDialog.Builder(TaymayContext)
    builder.setMessage(
        "${TaymayContext.getString(R.string.check)}"
    ).setPositiveButton("Okay") { dialog, id ->
        val browserIntent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                "https://play.google.com/store/apps/details?id=" + TaymayContext.packageName
            )
        )
        TaymayContext.startActivity(browserIntent)
    }.setNegativeButton(TaymayContext.getString(R.string.cancel)) { dialog, id ->
        dialog.dismiss()
    }
    val alert = builder.create()
    alert.show()
}

fun taymayShowDialogRateAndFeedback(context: Context, onDone: () -> Unit) {
    TaymayContext = context

    DialogRateAndReview().showDialogRateAndFeedback(TaymayContext, onDone)
}

fun taymayOpenPolicyActivity(context: Context, pathPolicyAssetFile: String, calHome: Class<*>) {
    TaymayContext = context

    PolicyActivity.Open_Policy(
        TaymayContext, // Activity hiện tại
        pathPolicyAssetFile // tên file ở Assets
        , calHome
    )
}

fun taymayShowDialogFeedback(context: Context, onDone: () -> Unit) {
    TaymayContext = context

    DialogRateAndReview().showDialogFeedback(
        TaymayContext, onDone
    )
}

fun taymayLoadAndShowAdInLayout(
    context: Context,
    adName: String,
    viewContainer: LinearLayout
) {
    loadAndShowAdInLayout(context, adName, viewContainer)
}


fun taymayShowDialogRemoveAd(context: Context, idProducts: String, clasHome: Class<*>) {
    TaymayContext = context
    if (TaymayContext is Activity) {
        val dialogRemoveAd = DialogRemoveAd(TaymayContext as Activity)
        dialogRemoveAd.showDialogRemoveAd(
            idProducts,
            clasHome
        )
    } else {
        elog("showDialogRemoveAd AppContext not is Activity")
    }

}


fun taymayShowDialogAdLoading(
    context: Context,
    adName: String, done: (isCanShow: Boolean, myAd: MyAd) -> Unit
) {
    TaymayContext = context

    showDialogLoadAd(context, adName, done)
}


fun setTextLoading(str: String, tvHello: TextView) {
    var list = TaymayApplication.getLines(str)
    GlobalScope.launch {
        var i = 0
        while (i < 250) {
            Thread.sleep(400)
            MainScope().launch {
                tvHello.text = list.shuffled().get(0)
                i++
            }
        }
    }


}


fun taymayGetAdsToCache(
    context: Context,
    vararg adNames: String
) {
    TaymayContext = context

    loadAdsInBackgroud(*adNames)
}

fun taymayLoadAdShowCallback(
    context: Context,
    adName: String, viewContainer: LinearLayout, callback: (myAd: MyAd) -> Unit
) {
    TaymayContext = context

    loadAndShowAd(context, adName, viewContainer, callback)

}

fun taymayInitReturnAppAd(context: Context, nameOpenAd: String) {
    TaymayContext = context
    if (OPEN_AD_INITED) return
    if (TaymayContext is Activity) {
        OPEN_AD_INITED = true
        val ad = getAdByAdName(nameOpenAd)
        if (ad != null && ad.isCanShow(
                TaymayContext
            )
        ) {
            when (ad.adNet) {
                AdNet.Admob -> {
                    AdmobAdOpen((TaymayContext as Activity).application, ad)
                }

                else -> {}
            }
        }
    } else {
        elog("Can not setup Return App --> AppContext is not Activity")
    }
}

fun taymayIsPayRemoveAd(context: Context): Boolean {
    TaymayContext = context
    try {
        return MyCache.getBooleanValueByName(
            TaymayApplication,
            IS_PREMIUM
        ) || !taymayIsCanRequestAds(
            context
        )
    } catch (e: Exception) {
        return MyCache.getBooleanValueByName(TaymayApplication, IS_PREMIUM)

    }
}
