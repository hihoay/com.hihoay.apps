package app.module.objecs

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import app.module.activities.AdReturnAppActivity
import app.module.utils.AD_NAME
import app.module.utils.TaymayContext
import app.module.utils.taymayIsCanShowOrLoadNow
import app.module.utils.taymayIsPayRemoveAd
import java.util.*

class AdmobAdOpen(val application: Application, var myAd: MyAd) :
    ActivityLifecycleCallbacks, LifecycleObserver {

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        loadAd(myAd.ad_name, LinearLayout(TaymayContext)) { isCanShow, ad ->
            if (isCanShow) myAd = ad
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (taymayIsPayRemoveAd(TaymayContext)) return
        showAdIfAvailable()
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        TaymayContext = activity
    }

    override fun onActivityResumed(activity: Activity) {
        TaymayContext = activity
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
//        CurrentAcvitiy = null
    }

    fun showAdIfAvailable() {
        //elog("showAdIfAvailable", "adManager.isCanShowNow()", adManager.isCanShowNow())
        if (taymayIsCanShowOrLoadNow(TaymayContext, myAd.ad_name) && myAd.isAdLoaded()) {
            //elog("open return app ad")
            var intent = Intent(
                TaymayContext, AdReturnAppActivity::class.java
            )
            intent.putExtra(AD_NAME, myAd.ad_name)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            TaymayContext.startActivity(
                intent
            )
        } else loadAd(
            myAd.ad_name,
            LinearLayout(TaymayContext)
        ) { isCanShow, ad ->
            if (isCanShow) myAd = ad
        }

    }

}