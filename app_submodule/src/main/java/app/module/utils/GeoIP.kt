package app.module.utils

import app.module.objecs.taymayGetJsonFromUrlByKtor
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


data class GeoIP(
    var country_code: String = "-",
    var country_name: String = "-",
    var city: String = "-",
    var postal: String = "-",
    var latitude: String = "-",
    var longitude: String = "-",
    var IPv4: String = "-",
    var state: String = "-",
    var jsonOriginal: String = "{}"
) {
    override fun toString(): String {
        return "GeoIP(country_code='$country_code', country_name='$country_name', city='$city', postal='$postal', latitude='$latitude', longitude='$longitude', IPv4='$IPv4', state='$state', jsonOriginal='$jsonOriginal')"
    }
}

fun initGeoIP(onDone: (geoIp: GeoIP) -> Unit) {
    try {
        taymayGetJsonFromUrlByKtor(
            "https://geolocation-db.com/json/67273a00-5c4b-11ed-9204-d161c2da74ce",
            "{}"
        ) {
            var geoIP = Gson().fromJson(
                it, GeoIP::class.java
            )
            geoIP.jsonOriginal = it
            MainScope().launch {
                if (it != "{}") {
                    staticGeoIP = geoIP
                    elog(it)
                    onDone(geoIP)
                } else onDone(GeoIP())
            }
        }
    } catch (e: Exception) {
        elog(e.message!!)
    }
}

fun taymayGetGeoIP(onDone: (geoIp: GeoIP) -> Unit) {
    try {
        if (staticGeoIP != null) {
            MainScope().launch {
                onDone(staticGeoIP!!)
            }
        } else
            initGeoIP(onDone)
    } catch (e: Exception) {
        elog(e.message!!)
    }
}