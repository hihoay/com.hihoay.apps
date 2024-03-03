package app.module.lang

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale


fun setLocale(activity: Activity, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources: Resources = activity.resources
    val config: Configuration = resources.getConfiguration()
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.getDisplayMetrics())
//
//
//    val displayMetrics: DisplayMetrics = resources.displayMetrics
//    val configuration = resources.configuration
//    configuration.setLocale(Locale(languageCode))
//    resources.updateConfiguration(configuration, displayMetrics)
//    configuration.locale = Locale(languageCode)
//    resources.updateConfiguration(configuration, displayMetrics)
//
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//        configuration.setLocale(locale)
//    } else
//        configuration.locale = locale
//
//    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
//        activity.createConfigurationContext(configuration)
//    } else {
//        resources.updateConfiguration(configuration, displayMetrics)
//    }

}