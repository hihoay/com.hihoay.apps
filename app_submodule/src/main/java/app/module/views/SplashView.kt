package app.module.views

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import app.module.R
import app.module.databinding.LayoutSplashLoadingBinding
import app.module.utils.setTextLoading


fun Activity.taymaySplashView(
    iconAnim: Int,
    layoutInflater: LayoutInflater
): LayoutSplashLoadingBinding {
    var layoutSplashLoadingBinding: LayoutSplashLoadingBinding = DataBindingUtil.inflate(
        layoutInflater, R.layout.layout_splash_loading, null, false
    )
    layoutSplashLoadingBinding.animSplash.setAnimation(iconAnim)
    try {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val version = pInfo.versionName
        layoutSplashLoadingBinding.tvInfor.text =
            resources.getString(R.string.app_name2) + " - Build " + version
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

//fun Application.getLines(f: String): List<String> {
//    var inputStream = assets.open(f)
//    val size: Int = inputStream.available()
//    val buffer = ByteArray(size)
//    inputStream.read(buffer)
//  return String(buffer).split("\n").shuffled()
//}


