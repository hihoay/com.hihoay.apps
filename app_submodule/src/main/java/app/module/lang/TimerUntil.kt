package app.module.lang

import android.content.Context
import java.util.Timer
import java.util.TimerTask

fun Context.timerInterval(time: Long, callBack: () -> Unit) {
    Timer().schedule(object : TimerTask() {
        override fun run() {
            callBack()
        }
    }, time)
}