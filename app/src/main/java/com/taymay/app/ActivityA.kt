package com.taymay.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.module.utils.taymayActivityLoadAndShowAdCallbackWaitFor

class ActivityA : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var textView: TextView = TextView(this)
        textView.text = "A"
        setContentView(textView)
        textView.setOnClickListener {
            taymayActivityLoadAndShowAdCallbackWaitFor(this, "open_app") { ad, a ->
                startActivity(Intent(this, ActivityB::class.java))
            }
        }
    }

}