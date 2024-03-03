package app.module.utils

import android.content.Context
import app.module.objecs.taymayGetDataBoolean
import app.module.objecs.taymayGetUserID
import app.module.objecs.taymayPostJsonToUrlByKtor
import com.google.gson.Gson


val KEY_TRACKING: String="event_tracking"

data class Tracker(
    var uid: String,
    var from: String,
    var ad_version: String,
    var data_version: String,
    var version_code: String,
    var version_name: String,
    var key: String,
    var event_number: Int,
    var event: String,
    var event_before: String
) {
    override fun toString(): String {
        return "Tracker(uid='$uid', from='$from', ad_version='$ad_version', data_version='$data_version', version_code='$version_code', version_name='$version_name', key='$key', event_number=$event_number, event='$event', event_before='$event_before')"
    }


}

/**
 * isCount để đếm xem nó xảy ra ở sự kiện thứ mấy trong 1 phiên
 */
fun Context.taymayEventTracking(
    isCount: Boolean,
    event: String = "",
) {
    try {
        var tracker = Tracker(
            uid = taymayGetUserID(this)!!,
            from = packageName,
            ad_version = AD_CONFIG_VERSION_DEFAULT,
            data_version = DATA_CONFIG_VERSION_DEFAULT,
            version_code = taymayGetAppVersionCode(),
            version_name = taymayGetAppVersionName(),
            key = KEY_TRACKING,
            event_number = if (isCount) upCount(KEY_TRACKING, 1, 0) else -1,
            event = event,
            event_before = MyCache.getStringValueByName(this, "event_before", "root")
        )
        if (
            taymayGetDataBoolean("event_tracking", true)
        ) {
            MyCache.putStringValueByName(this, "event_before", event)
            taymayPostJsonToUrlByKtor("https://bot.taymay.io/logger", Gson().toJson(tracker), "") {}
            elog(tracker.toString())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}



