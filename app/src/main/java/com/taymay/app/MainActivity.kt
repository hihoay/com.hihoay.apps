package com.taymay.app

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.module.objecs.taymayGetDataString
import app.module.objecs.taymayGetJsonFromUrlByKtor
import app.module.objecs.taymayGetUserID
import app.module.objecs.taymayPostJsonToUrlByKtor
import app.module.utils.taymayAskExitApp
import app.module.utils.taymayClearStackAndGoActivity
import app.module.utils.taymayDownloadFileFromUrl
import app.module.utils.taymayFirebaseEventTracking
import app.module.utils.taymayFirebaseScreenTracking
import app.module.utils.taymayGetAdVersion
import app.module.utils.taymayGetGeoIP
import app.module.utils.taymayGoHomeScreen
import app.module.utils.taymayLog
import app.module.utils.taymayOpenMoreAppActivity
import app.module.utils.taymayOpenSetLangActivity
import com.taymay.chatbot.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    fun getDemoView(): TextView {
        var tvHello = TextView(this)
        tvHello.setText("xin chao cac ban")
        tvHello.setTextColor(Color.BLUE)
        return tvHello
    }

    lateinit var activityBinding: ActivityMainBinding

    override fun onBackPressed() {
        taymayAskExitApp(this, "banner_bottom_coll", 2000) {
            it.finishAffinity()
            finishAffinity()
        }
    }
    override fun onResume() {
        super.onResume()
//        if (activityBinding != null)
//            showAdInView(this, "banner_bottom_coll", activityBinding.llAd3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainBinding.inflate(layoutInflater)
        taymayGetAdVersion(this) {
        }

    }
}