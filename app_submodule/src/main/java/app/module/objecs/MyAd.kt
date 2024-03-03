package app.module.objecs

import android.content.Context
import androidx.lifecycle.MutableLiveData
import app.module.enums.AdFormat
import app.module.enums.AdNet
import app.module.enums.AdState
import app.module.utils.AdShowLastTime
import app.module.utils.IS_TESTING
import app.module.utils.MyCache
import app.module.utils.TaymayContext
import app.module.utils.adsConfig
import app.module.utils.elog
import app.module.utils.taymayFirebaseScreenTracking
import app.module.utils.pushNotify
import java.io.Serializable

class MyAd : Serializable {
    var ad_network: String = ""
    var ad_id: String = ""
    var id: String = ""
    var app_id: String = ""
    var app_version_name: String = ""
    var ad_name: String = ""
    var ad_format: String = ""
    var ad_impressions = 10000
    var ad_distance = 25
    var ad_timeout = 15
    var ad_note: String = ""
    var ad_ctr = 20
    var ad_max_load = 15
    var ad_index = 0
    var ad_enable = false
    var ad_reload = false
    var ad_recycle = false
    var ad_update: String = ""
    var ad_version: String = ""
    var ad_bidding = false
    var ad_tag = "banner"

    var ad_cache: Any? = null
    var adFormat = AdFormat.Null
    var adState = AdState.Error
    var adNet = AdNet.Null

    val adStateLive = MutableLiveData<MyAd>()

    constructor(ad_name: String) {
        this.ad_name = ad_name
    }

    constructor() {}

    fun logAdShow(context: Context) {
        val show = MyCache.getIntValueByName(context, id)
        MyCache.putIntValueByName(context, id, show + 1)
        if (adFormat == AdFormat.Interstitial || adFormat == AdFormat.OpenAd) MyCache.putLongValueByName(
            context,
            AdShowLastTime,
            System.currentTimeMillis()
        )
    }


    fun isCanShow(context: Context): Boolean {
        val show = MyCache.getIntValueByName(context, id, 0)
        if (show >= ad_impressions) return false
        return if (ad_impressions < 0) false else true
    }

    fun setState(adCache: Any?, adState: AdState) {
        if (adState.toText() == this@MyAd.adState.toText()) return // Chỉ cập nhật trạng thái khi thay đổi trạng thái

        adsConfig.forEach {// Cập nhật lại trạng thái và cache cho tất cả những quảng cáo nào có cùng 1 mã
            if (it.ad_id == this.ad_id) {
                it.ad_cache = adCache
                it.adState = adState
            }
        }

        try { // Live data cập nhật trạng thái của Quảng cáo
            adStateLive.postValue(this@MyAd)
        } catch (e: Exception) {
        }
        elog(toString())



        if (adFormat == AdFormat.Interstitial || adFormat == AdFormat.OpenAd || adFormat == AdFormat.Reward || adFormat == AdFormat.Interstitial_Reward) // Lưu lại số lần hiển thị của các quảng cáo đã show
            when (adState) {
                AdState.Show -> {
                    logAdShow(TaymayContext)
                }

                else -> {}
            }




        when (adState) {
            AdState.Done, AdState.Close, AdState.Error, AdState.Timeout, AdState.Show -> kotlin.run { // Lựa chọn xóa cache hoặc giữ lại nếu muốn tái sử dụng lại cache thông qua thuộc tính ad_recycle
                adsConfig.forEach {
                    if (it.ad_id == this.ad_id && !it.ad_recycle) {
                        it.ad_cache = null
                        it.adState = adState
                    }
                }
            }

            else -> {}
        }

        when (adFormat) { // Log screen các màn hình quảng cáo nếu muốn log nó trên Firebase
            AdFormat.Interstitial, AdFormat.Interstitial_Reward, AdFormat.Reward, AdFormat.OpenAd -> {
                TaymayContext.taymayFirebaseScreenTracking(ad_name, ad_name)
            }

            else -> {
            }
        }

        try {
            if (IS_TESTING) // Nếu là test thì cho phép hiển thị quảng cáo của ứng dụng
                pushNotify("${adState.name}: $ad_name", "${adFormat.name} - $ad_tag - $ad_id")
        } catch (e: Exception) {
            elog("------------------", e.message!!)
        }
    }

    @JvmName("getAdFormat1")
    fun getAdFormat(): AdFormat {
        adFormat =
            if (ad_format == AdFormat.Banner.toText()) {
                AdFormat.Banner
            } else if (ad_format == AdFormat.Native.toText()) {
                AdFormat.Native
            } else if (ad_format == AdFormat.Interstitial.toText()) {
                AdFormat.Interstitial
            } else if (ad_format == AdFormat.Reward.toText()) {
                AdFormat.Reward
            } else if (ad_format == AdFormat.OpenAd.toText()) {
                AdFormat.OpenAd
            } else if (ad_format == AdFormat.Interstitial_Reward.toText()) {
                AdFormat.Interstitial_Reward
            } else AdFormat.Null

        return adFormat
    }

    @JvmName("setAdFormat1")
    fun setAdFormat(adFormat: AdFormat) {
        this.adFormat = adFormat
    }

    @JvmName("getAdNet1")
    fun getAdNet(): AdNet {
        adNet = if (ad_network == AdNet.Admob.toText()) {
            AdNet.Admob
        } else if (ad_network == AdNet.Cross.toText()) {
            AdNet.Cross
        } else if (ad_network == AdNet.Netlink.toText()) {
            AdNet.Netlink
        } else if (ad_network == AdNet.Pangle.toText()) {
            AdNet.Pangle
        } else {
            AdNet.Null
        }
        return adNet
    }

    @JvmName("setAdNet1")
    fun setAdNet(adNet: AdNet) {
        this.adNet = adNet
    }

    fun isAdReload(): Boolean {
        try {
            return ad_reload;
        } catch (e: Exception) {
        }
        return false
    }

    fun isAdLoaded(): Boolean {
        try {
            if (adState == AdState.Loaded && ad_cache != null) return true;
        } catch (e: Exception) {
        }
        return false;
    }


    override fun toString(): String {
        try {
            return TablePrinter().generateTable(
                arrayOf(
                    "ad_version",
                    "ad_name",
                    "ad_id",
                    "ad_network",
                    "ad_format",
                    "ad_tag",
                    "ad_impressions",
                    "ad_recycle",
                    "ad_distance",
                    "ad_timeout",
                    "ad_note",
                    "ad_ctr",
                    "ad_max_load",
                    "ad_enable",
                    "ad_update",
                    "ad_bidding",
                    "ad_tag",
                    "ad_cache",
                    "adFormat",
                    "adState",
                    "ad_reload",
                    "adNet"
                ).toMutableList(),
                listOf(
                    listOf(
                        ad_version.toString(),
                        ad_name.toString(),
                        ad_id.toString(),
                        ad_network.toString(),
                        ad_format.toString(),
                        ad_tag.toString(),
                        ad_impressions.toString(),
                        ad_recycle.toString(),
                        ad_distance.toString(),
                        ad_timeout.toString(),
                        ad_note.toString(),
                        ad_ctr.toString(),
                        ad_max_load.toString(),
                        ad_enable.toString(),
                        ad_update.toString(),
                        ad_bidding.toString(),
                        app_id.toString(),
                        ad_cache.toString(),
                        adFormat.toString(),
                        adState.toString(),
                        ad_reload.toString(),
                        adNet.toString()
                    )
                )
            )
        } catch (e: Exception) {
            return e.message.toString()
        }

    }

    fun formatLog(num: Int, str: Any?): String {
        try {
            return String.format(
                "%-${num}s", str.toString()
            )
        } catch (e: Exception) {
        }
        return ""

    }

    companion object {
        fun toTable(myads: MutableList<MyAd>) {
            try {
                elog(
                    TablePrinter().generateTable(
                        arrayOf(
                            "ad_version",
                            "ad_name",
                            "ad_id",
                            "ad_network",
                            "ad_format",
                            "ad_tag",
                            "ad_impressions",
                            "ad_recycle",
                            "ad_distance",
                            "ad_timeout",
                            "ad_note",
                            "ad_ctr",
                            "ad_max_load",
                            "ad_enable",
                            "ad_update",
                            "ad_bidding",
                            "ad_tag",
                            "ad_cache",
                            "adFormat",
                            "adState",
                            "ad_reload",
                            "adNet"
                        ).toMutableList(),
                        myads.map {
                            listOf(
                                it.ad_version.toString(),
                                it.ad_name.toString(),
                                it.ad_id.toString(),
                                it.ad_network.toString(),
                                it.ad_format.toString(),
                                it.ad_tag.toString(),
                                it.ad_impressions.toString(),
                                it.ad_recycle.toString(),
                                it.ad_distance.toString(),
                                it.ad_timeout.toString(),
                                it.ad_note.toString(),
                                it.ad_ctr.toString(),
                                it.ad_max_load.toString(),
                                it.ad_enable.toString(),
                                it.ad_update.toString(),
                                it.ad_bidding.toString(),
                                it.app_id.toString(),
                                it.ad_cache.toString(),
                                it.adFormat.toString(),
                                it.adState.toString(),
                                it.ad_reload.toString(),
                                it.adNet.toString()
                            )
                        }
                    )
                )
            } catch (e: Exception) {

            }

        }

    }
}