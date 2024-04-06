package com.taymay.app

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.module.utils.taymayAskExitApp
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