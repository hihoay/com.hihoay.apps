package app.module.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation.REVERSE
import android.view.animation.AnimationUtils
import app.module.R
import app.module.databinding.ActivityLanguageBinding
import app.module.lang.AdapterLanguage
import app.module.lang.CompassObject
import app.module.lang.JsonToList
import app.module.lang.Language
import app.module.lang.getData
import app.module.lang.readJsonFile
import app.module.lang.setData
import app.module.lang.setLocale
import app.module.lang.timerInterval
import app.module.lang.toObject
import app.module.objecs.loadAndShowAdInLayout
import app.module.utils.MyCache
import app.module.utils.taymayFirebaseScreenTracking
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.util.Locale

class SetupLanguageActivity : Activity() {
    lateinit var adapter: AdapterLanguage
    lateinit var binding: ActivityLanguageBinding

    companion object {
        var LANGUAGE_PREF = "LanguageActivity"
        var IS_SET_LANG = "IS_SET_LANG"

        //        var claHome: Class<*>? = null
        var adTopLanguageName = "au:language_top_small"
        var adBottomLanguageName = "au:language_bottom_small"
        var First = "First"
//        var local = mutableListOf(
//            "sq_AL",
//            "ar_DZ",
//            "ar_BH",
//            "ar_EG",
//            "ar_IQ",
//            "ar_JO",
//            "ar_KW",
//            "ar_LB",
//            "ar_LY",
//            "ar_MA",
//            "ar_OM",
//            "ar_QA",
//            "ar_SA",
//            "ar_SD",
//            "ar_SY",
//            "ar_TN",
//            "ar_AE",
//            "ar_YE",
//            "be_BY",
//            "bg_BG",
//            "ca_ES",
//            "zh_CN",
//            "zh_SG",
//            "zh_HK",
//            "zh_TW",
//            "hr_HR",
//            "cs_CZ",
//            "da_DK",
//            "nl_BE",
//            "nl_NL",
//            "en_AU",
//            "en_CA",
//            "en_IN",
//            "en_IE",
//            "en_MT",
//            "en_NZ",
//            "en_PH",
//            "en_SG",
//            "en_ZA",
//            "en_GB",
//            "en_US",
//            "et_EE",
//            "fi_FI",
//            "fr_BE",
//            "fr_CA",
//            "fr_FR",
//            "fr_LU",
//            "fr_CH",
//            "de_AT",
//            "de_DE",
//            "de_LU",
//            "de_CH",
//            "el_CY",
//            "el_GR",
//            "iw_IL",
//            "hi_IN",
//            "hu_HU",
//            "is_IS",
//            "in_ID",
//            "ga_IE",
//            "it_IT",
//            "it_CH",
//            "ja_JP",
//            "ko_KR",
//            "lv_LV",
//            "lt_LT",
//            "mk_MK",
//            "ms_MY",
//            "mt_MT",
//            "no_NO",
//            "pl_PL",
//            "pt_BR",
//            "pt_PT",
//            "ro_RO",
//            "ru_RU",
//            "sr_BA",
//            "sr_ME",
//            "sr_RS",
//            "sk_SK",
//            "sl_SI",
//            "es_AR",
//            "es_BO",
//            "es_CL",
//            "es_CO",
//            "es_CR",
//            "es_DO",
//            "es_EC",
//            "es_SV",
//            "es_GT",
//            "es_HN",
//            "es_MX",
//            "es_NI",
//            "es_PA",
//            "es_PY",
//            "es_PE",
//            "es_PR",
//            "es_ES",
//            "es_US",
//            "es_UY",
//            "es_VE",
//            "sv_SE",
//            "th_TH",
//            "tr_TR",
//            "uk_UA",
//            "vi_VN"
//        )


        var callbackSetupLanguage = fun(): Unit {}
        fun getUsedLangName(context: Context): String {
            var x = getData(context as Activity, LANGUAGE_PREF);
            if (x == "" && CompassObject.language.flag == "") {
                return "English"
            } else return x.toObject<Language>().local
        }

        fun isLanguageApplied(activity: Activity, callback: (b: Boolean) -> Unit) {
            val x = getData(activity, LANGUAGE_PREF)
            callback((x == ""))
        }

        fun getUsedLangCode(context: Context): String {
            var x = getData(context as Activity, LANGUAGE_PREF);
            if (x == "" && CompassObject.language.flag == "") {
                return "en"
            } else return x.toObject<Language>().lang_code
        }


        fun check(activity: Activity, function: () -> Unit) {
            callbackSetupLanguage = function
            var x = getData(activity, LANGUAGE_PREF);
            if (x == "") {
                val languages = mutableListOf<Language>()
                languages.addAll(activity.readJsonFile("flags.json").JsonToList<Language>())
                languages.sortBy { it.local }
                var us = languages.indexOfFirst { it.country_code == "US" }
                var a = languages[us]
                setLocale(activity, a.lang_code.toString())
                activity.startActivity(Intent(activity, SetupLanguageActivity::class.java))
                return
            }
            val y = x.toObject<Language>()
            CompassObject.language = y
            setLocale(activity, CompassObject.language.lang_code.toString())
            callbackSetupLanguage()
        }



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)



        loadAndShowAdInLayout(this, adTopLanguageName, binding.llAdTop)
        loadAndShowAdInLayout(this, adBottomLanguageName, binding.llAdBottom)
        showDone()
    }


    var languages = mutableListOf<Language>()

    private fun initActivity() {
        Firebase.analytics.logEvent("set_language_open", Bundle())
        readJsonFile("flags.json").JsonToList<Language>()
            .distinctBy { it.lang_code }
            .forEach {
                languages.add(it)
            }
        languages.sortBy { it.local }
        var us = languages.indexOfFirst { it.lang_code == "en" }
        var a = languages[us]
        languages.remove(a)
        languages.add(0, a)
        try {
            var lc =
                languages.indexOfFirst { it.lang_code == Locale.getDefault().language.toString() }
            a = languages[lc]
            languages.remove(a)
            languages.add(0, a)
        } catch (e: Exception) {
        }

        var x = getData(this, LANGUAGE_PREF);

        binding.icSave.visibility = View.INVISIBLE

        if (x == "" && CompassObject.language.flag == "") {
            setData(this, LANGUAGE_PREF, languages[0].toJSON())
            CompassObject.language = languages[0]
            binding.icLanguageBack.visibility = View.GONE
            binding.icSave.visibility = View.VISIBLE
//            binding.tvSkip.visibility = View.VISIBLE
        } else {
            val y = x.toObject<Language>()
            CompassObject.language = y
//            binding.tvSkip.visibility = View.GONE
        }
        binding.icLanguageBack.setOnClickListener {
            onBackPressed()
        }
        adapter = AdapterLanguage(this) {
            CompassObject.language = it
            binding.icSave.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()
            playAnimation()
        }
//        binding.tvSkip.setOnClickListener {
//            binding.tvSkip.visibility = View.GONE
//            setData(this, LANGUAGE, CompassObject.language.toJSON())
//            setLocale(this, Locale.getDefault().language)
//            Firebase.analytics.logEvent(
//                "set_language_localization_${!(Locale.getDefault().language == "en")}",
//                Bundle()
//            )

//            binding.icSave.visibility = View.INVISIBLE
//            binding.toastSaved.visibility = View.VISIBLE
//            try {
//                timerInterval(2000) {
//                    try {
//                        startActivity(Intent(this, getHomeClass()))
//                        finish()
//                    } catch (E: Exception) {
//                    }
//                }
//            } catch (E: Exception) {
//            }
//        }

        binding.icSave.setOnClickListener {
            setData(this, LANGUAGE_PREF, CompassObject.language.toJSON())
            setLocale(this, CompassObject.language.lang_code.toString())
            Firebase.analytics.logEvent(
                "set_language_localization_${!(CompassObject.language.lang_code.toString() == "en")}",
                Bundle()
            )
            MyCache.putBooleanValueByName(this, IS_SET_LANG, true)
            binding.icSave.visibility = View.INVISIBLE
            binding.toastSaved.visibility = View.VISIBLE
            try {
                timerInterval(2000) {
//                    try {

//                        if (claHome == null) {
                    callbackSetupLanguage()
                    finish()
//                        } else {
//                            var intent = Intent(this, claHome)
//                            intent.flags =
//                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            startActivity(intent)
//                            finish()
//                        }
//                    } catch (E: Exception) {
//                    }

                }
            } catch (E: Exception) {
            }
//            if (getData(this, First) != "")
//                startActivity(Intent(this, RootActivity::class.java))
//            else {
//                startActivity(Intent(this, RootActivity::class.java))
//                setData(this, First, First)
//            }
        }
//        var i = languages.indexOfFirst { getUsedLangName(this) == it.local }
        var i = languages.indexOfFirst { it.lang_code == Locale.getDefault().language.toString() }
        adapter.setData(languages)
        x = getData(this, LANGUAGE_PREF)
        if (x != "")
            binding.rcv.scrollToPosition(languages.indexOf(x.toObject()))
        binding.rcv.adapter = adapter
        binding.rcv.layoutManager!!.scrollToPosition(i)
    }

    override fun onResume() {
        taymayFirebaseScreenTracking("set_language_view", "SetupLanguageActivity")
        super.onResume()
    }

    private fun playAnimation() {
        var animTV = AnimationUtils.loadAnimation(this, R.anim.tv_set_lang)
        animTV.reset()
        animTV.repeatCount = 3
        animTV.repeatMode = REVERSE;
//        animTV.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(p0: Animation?) {
//            }
//            override fun onAnimationEnd(p0: Animation?) {
//                binding.icSave.startAnimation(animTV)
//            }
//            override fun onAnimationRepeat(p0: Animation?) {
//            }
//        })
        binding.icSave.clearAnimation()
        binding.icSave.startAnimation(animTV)
    }


    private fun showDone() {
        initActivity()
    }
}