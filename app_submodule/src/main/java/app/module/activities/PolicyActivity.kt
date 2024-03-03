package app.module.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import app.module.R
import app.module.utils.taymayFirebaseScreenTracking
import app.module.utils.showPrivacyOptionsForm

class PolicyActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //supportActionBar!!.hide()
        } catch (e: Exception) {
        }
        setContentView(R.layout.my_policy_activity)
        val wv: WebView
        wv = findViewById(R.id.wv_content)
        wv.loadUrl(intent.getStringExtra(POLICY_HTML)!!)
        findViewById<View>(R.id.btn_back_policy)
            .setOnClickListener { finish() }


        findViewById<View>(R.id.btn_policy_from).setOnClickListener {
            showPrivacyOptionsForm(calHome) { i, s ->
                startActivity(Intent(this, calHome).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }
    }

    override fun onResume() {
        taymayFirebaseScreenTracking("policy_view", "PolicyActivity")
        super.onResume()
    }

    companion object {
        lateinit var calHome: Class<*>

        const val POLICY_HTML = "Asset_Policy_HTML"

        @JvmStatic
        fun Open_Policy(context: Context, path_asset: String, calHome: Class<*>) {
            PolicyActivity.calHome = calHome
            var path_asset = path_asset
            path_asset = "file:///android_asset/$path_asset"
            val intent = Intent(context, PolicyActivity::class.java)
            intent.putExtra(POLICY_HTML, path_asset)
            context.startActivity(intent)
        }
    }
}