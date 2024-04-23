package app.module.objecs

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import app.module.ad_network.admob.AdmobManager
import app.module.databinding.DiglogLoadAdBinding
import app.module.enums.AdFormat
import app.module.enums.AdNet
import app.module.enums.AdState
import app.module.utils.AD_CONFIG_VERSION_DEFAULT
import app.module.utils.AD_VERSION
import app.module.utils.IAP_ID
import app.module.utils.IS_PREMIUM
import app.module.utils.IS_TESTING
import app.module.utils.MyCache
import app.module.utils.MyConnection
import app.module.utils.TAYMAY_AD_VERSION_API
import app.module.utils.TaymayContext
import app.module.utils.adsConfig
import app.module.utils.elog
import app.module.utils.taymayGetRemoteData
import app.module.utils.taymayIsPayRemoveAd
import app.module.utils.taymayLog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList


fun isAdVersionReady(): Boolean {
    return (adsConfig.size > 0 && MyConnection.taymayIsOnline(TaymayContext))
}

fun getAdsRemote(ready: () -> Unit) {
    if (adsConfig.size > 0 || !MyConnection.taymayIsOnline(TaymayContext)) {
        elog("AdsRemoted")
        ready()
        return
    }
    var link_remote = "$TAYMAY_AD_VERSION_API/${TaymayContext.packageName}.json"

    GlobalScope.launch {
        launch {
            elog("link_remote", link_remote)
            getAdConfigFromRemote({
//                try {
//                    TaymayContext.taymayLog("ad_configs", "remote")
//                } catch (e: Exception) {
//                }
                taymayGetJsonFromUrlByKtor(
                    link_remote,
                    "[]"
                ) { res ->
                    if (res != "[]") {
//                        try {
//                            TaymayContext.taymayLog("ad_configs", "default")
//                        } catch (e: Exception) {
//                        }
                        elog("ad_version from remote")
                    }
                    initAds(it, res)
                }
            }, {
                var ad_version = AD_CONFIG_VERSION_DEFAULT
                taymayGetJsonFromUrlByKtor(
                    link_remote,
                    "[]"
                ) {
                    if (it != "[]") {
//                        try {
//                            TaymayContext.taymayLog("ad_configs", "default")
//                        } catch (e: Exception) {
//                        }
                        elog("ad_version from default")
                    }
                    initAds(ad_version, it)
                }
            })
        }
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 10000 // Thời điểm kết thúc sau 20 giây (20,000 ms)
        elog("getAdsRemote start")
        while (System.currentTimeMillis() < endTime) {
            if (adsConfig.size > 0) {
                elog("getAdsRemote done")
                break
            }
        }
        elog("getAdsRemote timeout")

        if (adsConfig.isEmpty() && MyConnection.taymayIsOnline(TaymayContext)) {
//            try {
//                TaymayContext.taymayLog("ad_configs", "assets")
//            } catch (e: Exception) {
//            }
            elog("ad_version from assets")
            initAds(
                AD_CONFIG_VERSION_DEFAULT,
                TaymayContext.assets.open("ads.json").bufferedReader()
                    .use { it.readText() })
        }
        withContext(Dispatchers.Main) {
            ready()
        }
    }
}


fun getConfigByTag(name: String): Boolean {
    var flag: Boolean = false
    try {
        var tag = adsConfig.filter { it.ad_name == name }.first().ad_tag
        if (tag == "on") flag = true
        if (tag == "off") flag = false
    } catch (e: Exception) {
        flag = false
    }
    return flag
}

fun getAdConfigFromRemote(
    onDone: (ad_version: String) -> Unit, onError: () -> Unit
) {
    taymayGetRemoteData(AD_VERSION) {
        try {
            onDone(it)
            return@taymayGetRemoteData
        } catch (e: Exception) {
        }
        onError()

    }
}


fun initAds(ad_version_name: String, jsonString: String) {
    if (adsConfig.size > 0) return
    var ad_version = ad_version_name
    if (IS_TESTING) ad_version = AD_CONFIG_VERSION_DEFAULT
    AD_CONFIG_VERSION_DEFAULT = ad_version
    elog(
        "initAds", "ad_version", ad_version
    )
    try {
        elog(
            "--------------------->ad_version", ad_version_name,
            "$TAYMAY_AD_VERSION_API/${TaymayContext.packageName}.json"
        )


        var ads_from_server = Gson().fromJson<MutableList<MyAd>?>(
            jsonString, object : TypeToken<MutableList<MyAd>>() {}.type
        ).filter {
            it.ad_version != null && it.ad_version.equals(ad_version) && it.ad_enable
        }.toMutableList()
        adsConfig.clear()
        adsConfig.addAll(ads_from_server)
    } catch (e: Exception) {
        adsConfig = CopyOnWriteArrayList()
    }
    adsConfig = CopyOnWriteArrayList(adsConfig.filter {
        it.ad_id != null && it.ad_id.isNotEmpty() && it.app_id != null && it.app_id.isNotEmpty() && it.app_id.equals(
            TaymayContext.packageName
        ) && it.ad_enable
    })
    if (adsConfig.size > 0) {
        adsConfig.forEach {
            it.getAdNet()
            it.getAdFormat()
            it.id = UUID.randomUUID().toString()
        }
        MyAd.toTable(adsConfig)
        try {
            MyCache.putStringValueByName(
                TaymayContext,
                IAP_ID,
                taymayGetDataString("iap", "remote_ad")
            )
        } catch (e: Exception) {
        }
    }
}

fun loadAndShowAdInLayout(
    context: Context,
    adName: String,
    viewContainer: LinearLayout
) {
    TaymayContext = context
    loadAd(
        adName,
        viewContainer
    ) { isCanShow, myAd ->
        if (isCanShow) {
            MainScope().launch {
                showAd(
                    context,
                    adName,
                    viewContainer
                ) {
                }
            }
        }
    }
}


fun showDialogLoadAd(
    context: Context,
    adName: String,
    onComplete: (isCanShow: Boolean, myAd: MyAd) -> Unit
) {
    TaymayContext = context
    if (!MyConnection.taymayIsOnline(TaymayContext)) {
        onComplete(false, MyAd(adName))
        return
    }
    var builder = AlertDialog.Builder(TaymayContext)
    var loadAdBinding = DiglogLoadAdBinding.inflate(LayoutInflater.from(TaymayContext))
    builder.setView(loadAdBinding.root)
    builder.setCancelable(false)
    var dialog = builder.create()
    try {
        dialog.show()
    } catch (e: Exception) {
    }
    loadAd(adName, LinearLayout(TaymayContext)) { isCanShow, myAd ->
        try {
            if (dialog.isShowing) dialog.dismiss()
//            if (TaymayContext is Activity) if (!(TaymayContext as Activity).isDestroyed) {
//                if (dialog.isShowing) dialog.dismiss()
//            }
        } catch (e: Exception) {
        }
        onComplete(isCanShow, myAd)
    }
}


private fun initAdZone(ad: MyAd, viewContainer: LinearLayout?) {
    when (ad.adFormat) {
        AdFormat.Banner, AdFormat.Native -> {
            MyAdZone(TaymayContext)
                .showAdZone(ad, viewContainer)
        }

        else -> {}
    }
}

fun isCanLoad(ad: MyAd) {
    if (MyCache.getBooleanValueByName(
            TaymayContext,
            IS_PREMIUM,
            false
        )
    ) {
        ad.adState = AdState.Error
        return
    }

}

fun Application.loadAdsToCache(vararg adNames: String) {
    if (
        isAdVersionReady()
    ) loadAdsInBackgroud(*adNames)
}

fun loadAdsInBackgroud(
    vararg adNames: String
) {
    adNames.forEach {
        loadAd(it, LinearLayout(TaymayContext)) { isCanShow, myAd -> }
    }
}

fun isHasAd(adName: String): Boolean {
    try {
        return adsConfig.filter { it.ad_name.equals(adName) && it.ad_enable }
            .toMutableList().size > 0
    } catch (e: Exception) {
        return false
    }
}

fun isAdLoaded(adName: String): Boolean {
    try {
        return adsConfig.filter { it.ad_name.equals(adName) && it.ad_enable }
            .first().adState == AdState.Loaded
    } catch (e: Exception) {
        return false
    }
}

fun getAdByAdName(adName: String): MyAd {
    try {
        return adsConfig.filter { it.ad_name.equals(adName) }.sortedBy { it.ad_index }.first()
    } catch (e: Exception) {
        return MyAd(adName)

    }
}

fun getAdTimetut(adName: String): Int {
    try {
        return getAdByAdName(adName).ad_timeout * 1000// second
    } catch (e: Exception) {
        return 12 * 1000
    }
}

fun getValueByKey(key: String, defValue: String): String {
    // Config trên sheet 'key' là 'ad_name' còn 'value' là 'ad_id'
    try {
        return adsConfig.filter { it.ad_name.equals(key) }.sortedBy { it.ad_index }
            .firstOrNull()!!.ad_id
    } catch (e: java.lang.Exception) {
        return defValue
    }
}

fun loadAd(
    adName: String,
    viewContainer: LinearLayout,
    callback: (isCanShow: Boolean, myAd: MyAd) -> Unit
) {
    val adLoad: MyAd? = getAdByAdName(adName)
    if (adLoad != null && !taymayIsPayRemoveAd(TaymayContext)) {
        if (adLoad.adState == AdState.Loaded && adLoad.ad_cache != null) { // return và callback nếu quảng cáo đã tải rồi
            callback(true, adLoad)
            return
        }
        adLoad.setState(null, AdState.Init)
        isCanLoad(adLoad) // kiểm tra xem quảng cáo có thể hiển thị lên hay k, dành cho việc check thời gian show, số lần hiển thị, thanh toán remove ad trong app
        initAdZone(adLoad, viewContainer)
        when (adLoad.adNet) {
            AdNet.Admob -> {
                AdmobManager.loadAd(adLoad, viewContainer)
            }

            else -> {}
        }
        var time: Long = getAdTimetut(adName).toLong()
        var step: Int = 12
        var job: Job = GlobalScope.launch {
            repeat(step) {
                delay(time / step)
            }
            adLoad.setState(null, AdState.Timeout)
        }

        var ob: androidx.lifecycle.Observer<MyAd>? = null
        ob = androidx.lifecycle.Observer<MyAd> {
            when (it.adState) {
                AdState.Loaded -> {
                    job.cancel()
                    callback(true, it)
                    ob?.let { it1 -> adLoad.adStateLive.removeObserver(it1) }
                }

                AdState.Error, AdState.Timeout -> {
                    job.cancel()
                    callback(false, it)
                    ob?.let { it1 -> adLoad.adStateLive.removeObserver(it1) }
                }

                else -> {
                }
            }
        }
        adLoad.adStateLive.observeForever(ob)
    } else
        callback(false, MyAd(adName))
}

fun loadAndShowAd(
    context: Context,
    adName: String,
    viewContainer: LinearLayout,
    callback: (myAd: MyAd) -> Unit
) {
    TaymayContext = context

    loadAd(adName, viewContainer) { isCanShow, myAd ->
        if (isCanShow) {
            showAd(context, adName, viewContainer) {
                callback(myAd)
            }
        } else
            callback(myAd)
    }
}

fun showAd(
    context: Context,
    adName: String,
    viewContainer: LinearLayout,
    callback: (myAd: MyAd) -> Unit
) {
    TaymayContext = context
    val myAd: MyAd? = getAdByAdName(adName)
    if (myAd != null && !taymayIsPayRemoveAd(TaymayContext)) {
        MainScope().launch {
            when (myAd.adNet) {
                AdNet.Admob -> {
                    AdmobManager.showAd(myAd, viewContainer)
                }

                else -> {
                }
            }
        }
        var time: Long = getAdTimetut(adName).toLong()
        var step: Int = 12
        var job: Job = GlobalScope.launch {
            repeat(step) {
                delay(time / step)
            }
            myAd.setState(null, AdState.Timeout)
        }
        var ob: androidx.lifecycle.Observer<MyAd>? = null
        ob = androidx.lifecycle.Observer<MyAd> {
            when (it.adState) {
                AdState.Show -> {
                    callback(it)
                    if (it.adFormat == AdFormat.Banner || it.adFormat == AdFormat.Native)
                        ob?.let { it1 -> myAd.adStateLive.removeObserver(it1) }
                    job.cancel()
                }

                AdState.Close -> {
                    callback(myAd)
                    ob?.let { it1 -> myAd.adStateLive.removeObserver(it1) }
                }

                else -> {
                    callback(myAd)
                }
            }
        }
        myAd.adStateLive.observeForever(ob)
    } else callback(MyAd(adName))
}




