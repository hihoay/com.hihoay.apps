package app.module.objecs

import android.widget.LinearLayout

abstract class AdFormatManager {
    abstract fun loadAd(myAd: MyAd, viewContainer: LinearLayout)
    abstract fun showAd(myAd: MyAd, viewContainer: LinearLayout)
}