package app.module.utils

import android.content.Context

fun Context.getCountTime(name: String): Int {
    return MyCache.getIntValueByName(this, name, 0)
}

fun Context.upCountTime(name: String): Int {
    MyCache.putIntValueByName(this, name, MyCache.getIntValueByName(this, name, 0) + 1)
    return MyCache.getIntValueByName(this, name, 0)
}

fun Context.clearCountTime(name: String): Int {
    MyCache.putIntValueByName(this, name, 0)
    return MyCache.getIntValueByName(this, name, 0)
}