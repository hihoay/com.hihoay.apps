package app.module.utils

import android.content.Context

fun Context.getCount(name: String, def: Int): Int {
    return MyCache.getIntValueByName(this, name, def)
}
fun Context.setCount(name: String, value: Int) {
     MyCache.putIntValueByName(this, name, value)
}
fun Context.upCount(name: String, up: Int, def: Int): Int {
    MyCache.putIntValueByName(this, name, getCount(name, def) + up)
    return MyCache.getIntValueByName(this, name, def)
}

fun Context.downCount(name: String, down: Int, def: Int): Int {
    MyCache.putIntValueByName(this, name, getCount(name, def) - down)
    return MyCache.getIntValueByName(this, name, def)
}
