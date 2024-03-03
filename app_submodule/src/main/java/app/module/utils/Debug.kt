package app.module.utils

import android.content.Intent
import android.util.Log
import app.module.R

/**
 * Created by D on 8/8/2017.
 */
fun pushNotify(title: String, vararg msg: Any) {
    try {
        var log = ""
        for (obj in msg) {
            log = "$log - $obj"
        }
        elog(*msg)
        if (IS_TESTING)
            isGrantNotifyPermistion(TaymayContext) {
                if (it) notify(
                    TaymayContext,
                    Intent("Notify"),
                    title,
                    log,
                    R.mipmap.ic_launcher
                )

            }
    } catch (e: Exception) {
    }
}

fun elog(vararg massage: Any) {
    try {
        var log = ""
        for (obj in massage) {
            log = "$log   $obj"
        }
        if (IS_TESTING) {
            Log.e("Taymay-Log", log)
        }
    } catch (ez: Exception) {
    }
}
