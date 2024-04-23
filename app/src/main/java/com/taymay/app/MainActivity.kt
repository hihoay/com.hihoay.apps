package com.taymay.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.module.utils.taymayEventTracking
import app.module.utils.taymayFirebaseEventTracking
import app.module.utils.taymayGetAdVersion
import com.taymay.chatbot.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    lateinit var activityBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainBinding.inflate(layoutInflater)
        taymayGetAdVersion(this) {
        }


    }
}