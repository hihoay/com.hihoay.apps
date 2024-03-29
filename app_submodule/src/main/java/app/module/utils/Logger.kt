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
        val pInfo: PackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0)
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

fun Context.taymayLogNoGeo(
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
    taymayPostJsonToUrlByKtor(LOG_HOST, Gson().toJson(data), "") {}

}

fun Context.taymayLog(
    dimen: String,
    metric: Double = 0.0,
) {
    taymayGetGeoIP {
        var data = Data(
            uid = taymayGetUserID(this)!!,
            from = packageName,
            version_code = taymayGetAppVersionCode(),
            version_name = taymayGetAppVersionName(),
            key = dimen,
            double_value = metric,
            json_value = it.jsonOriginal
        )
        taymayPostJsonToUrlByKtor(LOG_HOST, Gson().toJson(data), "") {}
    }
}


fun Context.taymayLog(
    dimen: String,
    metric: Long = 0,
) {
    taymayGetGeoIP {
        var data = Data(
            uid = taymayGetUserID(this)!!,
            from = packageName,
            version_code = taymayGetAppVersionCode(),
            version_name = taymayGetAppVersionName(),
            key = dimen,
            long_value = metric,
            json_value = it.jsonOriginal
        )
        taymayPostJsonToUrlByKtor(LOG_HOST, Gson().toJson(data), "") {}
    }
}

fun Context.taymayLog(
    dimen: String,
    metric: String = "",
) {
    taymayGetGeoIP {
        var data = Data(
            uid = taymayGetUserID(this)!!,
            from = packageName,
            version_code = taymayGetAppVersionCode(),
            version_name = taymayGetAppVersionName(),
            key = dimen,
            string_value = metric,
            json_value = it.jsonOriginal
        )
        taymayPostJsonToUrlByKtor(LOG_HOST, Gson().toJson(data), "") {}
    }
}


fun Context.taymayLog(
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

        taymayPostJsonToUrlByKtor(LOG_HOST, Gson().toJson(data), "") {}
    }

}

