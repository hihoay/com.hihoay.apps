package app.module.lang

import android.app.Activity
import android.content.Context.MODE_PRIVATE

public fun setData(a: Activity, key: String, data: String) {
    val sharedPref = a.getSharedPreferences("AAAA", MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(key, data)
    editor.commit()
}

fun getData(a: Activity, key: String): String {
    val sharedPref = a.getSharedPreferences("AAAA", MODE_PRIVATE)
    val a = sharedPref.getString(key, "")
    return a.toString()
}

fun getData(a: Activity, key: String, def: String): String {
    val sharedPref = a.getSharedPreferences("AAAA", MODE_PRIVATE)
    val a = sharedPref.getString(key, "")
    if (a.isNullOrBlank()) return def
    return a.toString()
}
