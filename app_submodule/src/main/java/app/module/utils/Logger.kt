package app.module.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import app.module.objecs.taymayGetUserID
import app.module.objecs.taymayPostJsonToUrlByKtor
import com.google.gson.Gson


data class Data(
    var uid: String,
    var from: String,
    var version_code: String,
    var version_name: String,
    var key: String,
    var string_value: String = "",
    var long_value: Long = 0,
    var double_value: Double = 0.0,
    var json_value: String = ""
)

fun Context.taymayGetAppVersionCode(): String {
    try {
        val pInfo: PackageInfo =
            getPackageManager().getPackageInfo(getPackageName(), 0)
        val versionName: String = pInfo.versionCode.toString()

        return versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return "-";

}

fun Context.taymayGetAppVersionName(): String {

    try {
        val pInfo: PackageInfo =
            getPackageManager().getPackageInfo(getPackageName(), 0)
        val versionName: String = pInfo.versionName

        return versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return "-";

}

fun Context.taymayLogDataNoGeo(
    key: String,
    string_value: String = "",
    long_value: Long = 0,
    double_value: Double = 0.0
) {
    var data = Data(
        uid = taymayGetUserID(this)!!,
        from = packageName,
        version_code = taymayGetAppVersionCode(),
        version_name = taymayGetAppVersionName(),
        key = key,
        string_value = string_value,
        long_value = long_value,
        double_value = double_value,
    )
    taymayPostJsonToUrlByKtor("https://bot.taymay.io/logger", Gson().toJson(data), "") {}

}

fun Context.taymayLogData(
    key: String,
    string_value: String = "",
    long_value: Long = 0,
    double_value: Double = 0.0
) {
    taymayGetGeoIP {
        var data = Data(
            uid = taymayGetUserID(this)!!,
            from = packageName,
            version_code = taymayGetAppVersionCode(),
            version_name = taymayGetAppVersionName(),
            key = key,
            string_value = string_value,
            long_value = long_value,
            double_value = double_value,
            json_value = it.jsonOriginal
        )

        taymayPostJsonToUrlByKtor("https://bot.taymay.io/logger", Gson().toJson(data), "") {}
    }

}


fun Context.taymayLogString(key: String, string: String) {
    taymayGetGeoIP {
        var data = Data(
            uid = taymayGetUserID(this)!!,
            from = packageName,
            version_code = taymayGetAppVersionCode(),
            version_name = taymayGetAppVersionName(),
            key = key,
            string_value = string,
            json_value = it.jsonOriginal
        )

        taymayPostJsonToUrlByKtor("https://bot.taymay.io/logger", Gson().toJson(data), "") {}
    }

}

fun Context.taymayLogLong(key: String, longValue: Long) {
    taymayGetGeoIP {
        var data = Data(
            uid = taymayGetUserID(this)!!,
            from = packageName,
            version_code = taymayGetAppVersionCode(),
            version_name = taymayGetAppVersionName(),
            key = key,
            long_value = longValue,
            json_value = it.jsonOriginal
        )
        taymayPostJsonToUrlByKtor("https://bot.taymay.io/logger", Gson().toJson(data), "") {}
    }
}

fun Context.taymayLogDouble(key: String, double_value: Double) {
    taymayGetGeoIP {
        var data = Data(
            uid = taymayGetUserID(this)!!,
            from = packageName,
            version_code = taymayGetAppVersionCode(),
            version_name = taymayGetAppVersionName(),
            key = key,
            double_value = double_value,
            json_value = it.jsonOriginal
        )
        taymayPostJsonToUrlByKtor("https://bot.taymay.io/logger", Gson().toJson(data), "") {}
    }

}

