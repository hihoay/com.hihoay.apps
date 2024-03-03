package com.taymay.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ActivityB : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        var textView: TextView = TextView(this)
        textView.text = "B"
        textView.setOnClickListener {

        }
        setContentView(textView)

    }
}
