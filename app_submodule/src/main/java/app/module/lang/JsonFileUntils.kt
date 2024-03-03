package app.module.lang

import android.content.Context

fun Context.readJsonFile(fileName: String) =
    applicationContext.assets.open(fileName).bufferedReader().use { it.readText() }
