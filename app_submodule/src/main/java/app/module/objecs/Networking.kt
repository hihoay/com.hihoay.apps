package app.module.objecs

import android.content.Context
import app.module.utils.ID_USER
import app.module.utils.MyCache
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

fun taymayGetUserID(context: Context): String? {
    try {
        if (MyCache.getStringValueByName(context, ID_USER).isEmpty()) {
            val user_id: String = UUID.randomUUID().toString() + "-" + System.currentTimeMillis()
            MyCache.putStringValueByName(context, ID_USER, user_id)
        }
        return MyCache.getStringValueByName(context, ID_USER)
    } catch (e: java.lang.Exception) {
    }
    return "unknow"
}

fun taymayGetJsonFromUrlByKtor(url: String, defaultVault: String, callback: (res: String) -> Unit) {
    GlobalScope.launch {
        try {
            val client = HttpClient(Android)
            val response: HttpResponse = client.request(url) {
                method = HttpMethod.Get
            }
            if (response.status == HttpStatusCode.OK) {
                callback(response.body())
                return@launch
            }

        } catch (e: Exception) {
        }
        callback(defaultVault)

    }

}


fun taymayPostJsonToUrlByKtor(
    server: String,
    json: String,
    defaultVault: String,
    callback: (res: String) -> Unit
) {
    GlobalScope.launch {
        try {
            val client = HttpClient(Android)
            val response: HttpResponse = client.post(server) {
                contentType(ContentType.Application.Json)
                setBody(json)
            }
            if (response.status == HttpStatusCode.OK) {
                callback(response.body())
                return@launch
            }

        } catch (e: Exception) {
        }
        callback(defaultVault)
    }


}