package app.module.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.databinding.DataBindingUtil
import app.module.R
import app.module.databinding.MyLayoutAdExitAppBinding
import app.module.enums.AdFormat
import app.module.objecs.MyAd
import app.module.objecs.loadAndShowAdInLayout
import app.module.utils.taymayFirebaseScreenTracking
import app.module.utils.setTextLoading

class ExitAppActivity : Activity() {

    lateinit var myLayoutAdExitAppBinding: MyLayoutAdExitAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myLayoutAdExitAppBinding = DataBindingUtil.inflate(
            this@ExitAppActivity.layoutInflater,
            R.layout.my_layout_ad_exit_app,
            null,
            false
        )
        initViewAd()
        intent.getStringExtra("idads")?.let { ad_name ->

//            showAdInView(
//                this, ad_name, myLayoutAdExitAppBinding!!.adxNativeExit
//            )
            myLayoutAdExitAppBinding.layoutAds.visibility = View.VISIBLE
            myLayoutAdExitAppBinding.layoutExit.visibility = View.GONE
            myLayoutAdExitAppBinding.btnYesExit.setOnClickListener { toExitApp() }
            myLayoutAdExitAppBinding.btnNoExit.setOnClickListener { finish() }
            loadAndShowAdInLayout(
                this,
                ad_name,
                myLayoutAdExitAppBinding.adxNativeExit
            )

//            showAdLoading(this, ad_name) { isCanShow, myAd ->
//                kotlin.run {
//                    if (isCanShow) {
//                        initViewAd()
//                        showAdExitLoaded(myAd)
//                    } else {
//                        initViewAd()
//                        toExitApp()
//                    }
//                }
//
//            }
        }

    }

    private fun showAdExitLoaded(myAd: MyAd) {
        when (myAd.adFormat) {

            AdFormat.Native, AdFormat.Banner -> {

            }

            else -> {}
        }
    }

    private fun initViewAd() {
        myLayoutAdExitAppBinding!!.layoutAds.visibility = View.GONE
        myLayoutAdExitAppBinding!!.layoutExit.visibility = View.GONE
        setContentView(myLayoutAdExitAppBinding!!.root)
    }

    override fun onBackPressed() {}
    fun toExitApp() {
        myLayoutAdExitAppBinding.layoutAds.visibility = View.GONE
        myLayoutAdExitAppBinding.layoutExit.visibility = View.VISIBLE
        setTextLoading("goodbye.txt", myLayoutAdExitAppBinding.tvGoodbye)
        object : CountDownTimer(waitToExit, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                exitApp()
            }
        }.start()
    }

    override fun onResume() {
        taymayFirebaseScreenTracking("exit_app_view", "ExitAppActivity")
        super.onResume()
    }

    private fun exitApp() {
        runExitApp(this)
    }


    companion object {
        var runExitApp: (exitActivity: ExitAppActivity) -> Unit =
            fun(exitActivity: ExitAppActivity) {
            }
        var waitToExit: Long = 2000
        fun askExitApp(idAd: String?, homeActivity: android.app.Activity) {
            val intent = Intent(homeActivity, ExitAppActivity::class.java)
            intent.putExtra("idads", idAd)
            homeActivity.startActivity(intent)
        }
    }
}