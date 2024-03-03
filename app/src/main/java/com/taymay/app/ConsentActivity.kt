package com.taymay.app

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import app.module.utils.elog
import app.module.utils.taymayInitUMP

class ConsentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LinearLayout(this))
        taymayInitUMP(this) { b, consentInformation ->
            elog("callback", "taymayInitUMP", b)
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


}