package app.module.lang

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


interface JSONConvertable {
    fun toJSON(): String = gson.toJson(this)
}

fun toJson(l: List<JSONConvertable>): String {
    return gson.toJsonTree(l).toString()
}

inline fun <reified T : JSONConvertable> ArrayList<T>.toJson(): String {
    return gson.toJsonTree(this).toString()
}

inline fun <reified T : JSONConvertable> String.toObject(): T = gson.fromJson(this, T::class.java)

inline fun <reified T : JSONConvertable> String.JsonToList(): ArrayList<T> {
    try {
        val arrayTutorialType = object : TypeToken<ArrayList<T>>() {}.type
        val a: ArrayList<T> = gson.fromJson(this, arrayTutorialType)
        return a
    } catch (e: Exception) {

    }
    return arrayListOf()
}

//
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken

var gson = Gson()

//interface JSONConvertable {
//    fun toJSON(): String = gson.toJson(this)
//}
//
//inline fun <reified T : JSONConvertable> String.toObject(): T = gson.fromJson(this, T::class.java)

inline fun <reified T : JSONConvertable> String.toArrayList(): MutableList<T> {
    var json = this;
    val result: List<T>?
    if (json != "") {
        val list: ArrayList<Map<String, Any?>>? =
            fromJson(this)
        result = list?.mapNotNull {
            mapToObject(it, T::class.java)
        }
        return ArrayList(result)
    }

    return ArrayList()
}

inline fun <reified T : JSONConvertable> List<T>.toJSON() = gson.toJson(this)

//inline fun <reified T : JSONConvertable> List<T>.toJSONOfList() = gson.toJson(this)


inline fun <reified T> fromJson(json: String): T? {
    return gson.fromJson(json, object : TypeToken<T>() {}.type)
}

fun <T> mapToObject(map: Map<String, Any?>?, type: Class<T>): T? {
    if (map == null) return null

    val json = gson.toJson(map)
    return gson.fromJson(json, type)
}



