package com.taymay.app

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.module.utils.taymayAskExitApp
import app.module.utils.taymayGetAdVersion
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