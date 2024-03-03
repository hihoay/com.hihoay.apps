package app.module.objecs

import android.content.Context
import app.module.utils.DATA_CONFIG_VERSION_DEFAULT
import app.module.utils.DATA_VERSION
import app.module.utils.IS_TESTING
import app.module.utils.TAYMAY_DATA_VERSION_API
import app.module.utils.TaymayContext
import app.module.utils.elog
import app.module.utils.taymayGetRemoteData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList


data class DataConfig(
    var app_id: String = "",
    var bool_value: Boolean = false,
    var key: String = "",
    var data_version: String = "",
    var double_value: Double = 0.0,
    var long_value: Long = 0,
    var string_value: String = ""
) {

    override fun toString(): String {
        try {
            return TablePrinter().generateTable(
                arrayOf(
                    "data_version",
                    "app_id",
                    "key",
                    "bool_value",
                    "double_value",
                    "long_value",
                    "string_value"

                ).toMutableList(),
                listOf(
                    listOf(
                        data_version ?: "",
                        app_id ?: "",
                        key ?: "",
                        bool_value.toString() ?: "",
                        double_value.toString() ?: "",
                        long_value.toString() ?: "",
                        string_value ?: "",
                    )
                )
            )
        } catch (e: Exception) {
            return e.message.toString()
        }
    }

    companion object {
        fun toTable(dataConfigs: MutableList<DataConfig>) {
            try {
                elog(
                    TablePrinter().generateTable(
                        arrayOf(
                            "data_version",
                            "app_id",
                            "key",
                            "bool_value",
                            "double_value",
                            "long_value",
                            "string_value"
                        ).toMutableList(),
                        dataConfigs.map {
                            listOf(
                                it.data_version ?: "",
                                it.app_id ?: "",
                                it.key ?: "",
                                it.bool_value.toString() ?: "",
                                it.double_value.toString() ?: "",
                                it.long_value.toString() ?: "",
                                it.string_value ?: "",
                            )
                        }
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                e.message?.let { elog(it) }
            }
        }
    }
}


var isDataLoaded = false

private var dataConfigs: CopyOnWriteArrayList<DataConfig> = CopyOnWriteArrayList()

fun initDataVersion(context: Context, onInited: () -> Unit) {
    if (getDataConfigs().size > 0) {
        if (!isDataLoaded) {
            onInited()
            return
        }
    }
    GlobalScope.launch {
        var assetsDefault: String = ""
        try {
            assetsDefault = context.assets.open("data.json").bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            assetsDefault = "[]"
        }




        taymayGetRemoteData(DATA_VERSION) {
            var data_version = it
            if (it.isNullOrEmpty())
                data_version = DATA_CONFIG_VERSION_DEFAULT
            if (IS_TESTING) data_version = DATA_CONFIG_VERSION_DEFAULT
            DATA_CONFIG_VERSION_DEFAULT = data_version
            var link_data = "$TAYMAY_DATA_VERSION_API/${context.packageName}.json"
            taymayGetJsonFromUrlByKtor(
                link_data,
                assetsDefault
            ) { res ->

                elog(
                    "--------------------->data_version", data_version,
                    "$TAYMAY_DATA_VERSION_API/${TaymayContext.packageName}.json"
                )

                var data_configs = Gson().fromJson<MutableList<DataConfig>>(
                    res, object : TypeToken<MutableList<DataConfig>>() {}.type
                ).filter { item ->
                    item.data_version.equals(data_version)
                }.toMutableList()
                dataConfigs.clear()
                dataConfigs.addAll(data_configs)
                DataConfig.toTable(dataConfigs)
                MainScope().launch {
                    if (!isDataLoaded) {
                        isDataLoaded = true
                        onInited()
                    }

                }
            }
        }
    }

}

fun getDataConfigs(): CopyOnWriteArrayList<DataConfig> {
    return dataConfigs
}

fun taymayGetDataString(key: String, default_value: String): String {
    try {
        return dataConfigs.toMutableList().first { it.key == key }.string_value
    } catch (e: Exception) {
        elog(e.message!!)

        return default_value
    }
}

fun taymayGetDataBoolean(key: String, default_value: Boolean): Boolean {
    try {
        return dataConfigs.toMutableList().first { it.key == key }.bool_value
    } catch (e: Exception) {
        elog(e.message!!)
        return default_value
    }
}

fun taymayGetDataDouble(key: String, default_value: Double): Double {
    try {
        return dataConfigs.toMutableList().first { it.key == key }.double_value
    } catch (e: Exception) {
            e.message?.let { it1 -> elog(it1) }
        return default_value
    }
}

fun taymayGetDataLong(key: String, default_value: Long): Long {
    try {
        return dataConfigs.toMutableList().first { it.key == key }.long_value
    } catch (e: Exception) {
        elog(e.message!!)

        return default_value
    }
}