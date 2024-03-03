package app.module.views

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import app.module.R
import app.module.activities.AdInterActivity
import app.module.databinding.LayoutPreloadReturnAppBinding
import app.module.databinding.LayoutSplashLoadingBinding
import app.module.lang.timerInterval
import app.module.objecs.MyAd
import app.module.objecs.taymayGetDataLong
import app.module.utils.Config.anim_splash
import app.module.utils.setTextLoading
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


fun Activity.taymaySplashView(
    app_name: String,
    iconAnim: Int,
    layoutInflater: LayoutInflater
): LayoutSplashLoadingBinding {
    var layoutSplashLoadingBinding: LayoutSplashLoadingBinding = DataBindingUtil.inflate(
        layoutInflater, R.layout.layout_splash_loading, null, false
    )
    layoutSplashLoadingBinding.animSplash.setAnimation(iconAnim)
    layoutSplashLoadingBinding.imvSplash.visibility = View.GONE

    try {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val version = pInfo.versionName
        layoutSplashLoadingBinding.tvVersion.text = "Build $version"
        layoutSplashLoadingBinding.tvInfor.text =
            app_name
        layoutSplashLoadingBinding.tvCopyRight.text = ""
        layoutSplashLoadingBinding.layoutAppInfor.visibility = View.VISIBLE
        layoutSplashLoadingBinding.spinLoadNextScreen.visibility = View.GONE
    } catch (e: Exception) {
    }
    layoutSplashLoadingBinding.tvTipLoad.text =
        getString(R.string.this_action_may_contains_ads_n_remove_ads_if_you_find_it_annoying)


    setTextLoading("hello.txt", layoutSplashLoadingBinding.tvHello)
    return layoutSplashLoadingBinding

}


fun Context.waitToDone(ad: MyAd?, ac: AdInterActivity?, callback: () -> Unit) {
    ac?.let {
        timerInterval(taymayGetDataLong("ad_wait_to_done", 1)) {
            MainScope().launch {
                if (!ac.isDestroyed) ac.finish()
                callback()
            }
        }
    } ?: run {
        callback()
    }
}

fun Activity.taymaySplashViewNoAnim(
    app_name: String,
    iconAnim: Int,
    layoutInflater: LayoutInflater
): LayoutSplashLoadingBinding {
    var layoutSplashLoadingBinding: LayoutSplashLoadingBinding = DataBindingUtil.inflate(
        layoutInflater, R.layout.layout_splash_loading, null, false
    )
    layoutSplashLoadingBinding.animSplash.visibility = View.GONE
    layoutSplashLoadingBinding.imvSplash.setImageResource(iconAnim)
    try {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val version = pInfo.versionName
        layoutSplashLoadingBinding.tvVersion.text = "Build $version"
        layoutSplashLoadingBinding.tvInfor.text =
            app_name
        layoutSplashLoadingBinding.tvCopyRight.text = ""
        layoutSplashLoadingBinding.layoutAppInfor.visibility = View.VISIBLE
        layoutSplashLoadingBinding.spinLoadNextScreen.visibility = View.GONE
    } catch (e: Exception) {
    }
    layoutSplashLoadingBinding.tvTipLoad.text =
        getString(R.string.this_action_may_contains_ads_n_remove_ads_if_you_find_it_annoying)


    setTextLoading("hello.txt", layoutSplashLoadingBinding.tvHello)
    return layoutSplashLoadingBinding

}

fun Application.getLines(f: String): List<String> {
    var inputStream = assets.open(f)
    val size: Int = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    return String(buffer).split("\n").shuffled()
}


fun Activity.taymayLoadingView(
    layoutInflater: LayoutInflater
): LayoutPreloadReturnAppBinding {
    val layoutPreloadActivityBinding: LayoutPreloadReturnAppBinding =
        DataBindingUtil.inflate(layoutInflater, R.layout.layout_preload_return_app, null, false)
    layoutPreloadActivityBinding.animSplash.setAnimation(anim_splash)

    return layoutPreloadActivityBinding
}




