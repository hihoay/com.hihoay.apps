package app.module.utils

import app.module.objecs.MyAd
import com.google.android.ump.ConsentInformation
import java.util.concurrent.CopyOnWriteArrayList
var IS_TESTING: Boolean = true
var HASH_UMP_TEST: String = "abc"
var AdShowLastTime: String = "AdShowLastTime"
var OPEN_AD_INITED = false
var DATA_CONFIG_VERSION_DEFAULT = "default"
var AD_CONFIG_VERSION_DEFAULT = "default"
var IsCanShowAd = true
val AD_NAME: String = "AD_NAME"
val ID_USER: String = "USER_ID"
val IS_PREMIUM: String = "IS_PREMIUM"
val IAP_ID: String = "IAP_ID"
var adsConfig: CopyOnWriteArrayList<MyAd> = CopyOnWriteArrayList()
var TAYMAY_AD_VERSION_API = "https://bot.taymay.io/ad_version"
var AD_VERSION = "ad_version"
var DATA_VERSION = "data_version"
var TAYMAY_DATA_VERSION_API = "https://bot.taymay.io/data_version"
var PRODUCTS = "remove_ad"
var staticGeoIP: GeoIP? = null
