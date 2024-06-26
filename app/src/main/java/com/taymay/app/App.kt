package com.taymay.app

import android.app.Application
import app.module.utils.AD_CONFIG_VERSION_DEFAULT
import app.module.utils.HASH_UMP_TEST
import app.module.utils.IS_TESTING
import app.module.utils.taymaySetupApplication
import com.taymay.chatbot.BuildConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()
//        AD_CONFIG_VERSION_DEFAULT = "test"
//        DATA_CONFIG_VERSION_DEFAULT = "default"
        IS_TESTING = BuildConfig.DEBUG
        HASH_UMP_TEST = "F7EB8E91BAB6191FA8F87015557D41A5"
        taymaySetupApplication(this, "remove_ad,upgrade_01")
    }
}