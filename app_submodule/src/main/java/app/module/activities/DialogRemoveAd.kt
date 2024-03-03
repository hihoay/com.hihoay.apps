package app.module.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.BadTokenException
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import app.module.R
import app.module.databinding.DialogRemoveAdsBinding
import app.module.databinding.DialogUpgradedBinding
import app.module.objecs.taymayGetDataString
import app.module.utils.IS_PREMIUM
import app.module.utils.MyCache
import app.module.utils.TaymayApplication
import app.module.utils.elog
import app.module.utils.taymayGetGeoIP
import app.module.utils.taymayLogString
import com.limurse.iap.DataWrappers
import com.limurse.iap.IapConnector
import com.limurse.iap.PurchaseServiceListener

/**
val dialogRemoveAd = DialogRemoveAd(this@SettingAc)
dialogRemoveAd.showDialogRemoveAd(
Arrays.asList(
MyCache.getStringMetadata(this@SettingAc, META_REMOVE_ADS)
),
MyCache.getStringMetadata(this@SettingAc, META_REMOVE_ADS),
MainActivity::class.java
)
 */

class DialogRemoveAd(var context: Activity) {
    lateinit var dialogIAP: Dialog
    lateinit var dialogRemoveAdsBinding: DialogRemoveAdsBinding
    lateinit var iapConnector: IapConnector
    lateinit var dialogUpgradedBinding: DialogUpgradedBinding
    lateinit var product: String
    lateinit var clasHome: Class<*>
    var nonCons: List<String> = ArrayList()

    companion object {
        fun logToServer(purchaseInfo: DataWrappers.PurchaseInfo) {
            try {
                TaymayApplication.taymayLogString("purchased", purchaseInfo.originalJson)
                taymayGetGeoIP {
                    TaymayApplication.taymayLogString(
                        "buy-${purchaseInfo.sku}",
                        "${it.country_code}:${purchaseInfo.orderId}"
                    )
                }
            } catch (e: Exception) {

            }
        }
    }

    var purchaseServiceListener: PurchaseServiceListener = object : PurchaseServiceListener {


        override fun onPricesUpdated(iapKeyPrices: Map<String, List<DataWrappers.ProductDetails>>) {
            iapKeyPrices.forEach { entry -> elog(entry.key, entry.value) }
            if (iapKeyPrices.containsKey(product)) {
                dialogRemoveAdsBinding.tvCostPremium.text =
                    iapKeyPrices[product]?.first()?.price + " / Lifetime"
            } else {
                Toast.makeText(context, "Oops..Some Error..Try later!", Toast.LENGTH_SHORT)
                    .show()
                if (dialogIAP.isShowing) dialogIAP.dismiss()
                return
            }
            dialogRemoveAdsBinding.tvPay.setOnClickListener {
                iapConnector.purchase(context, product)
                dialogRemoveAdsBinding.tvPay.visibility = View.GONE
            }
            dialogRemoveAdsBinding.llDialogRemoveAd.visibility = View.VISIBLE
            dialogRemoveAdsBinding.llLoading.visibility = View.GONE
        }

        override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
            logToServer(purchaseInfo)
            MyCache.putBooleanValueByName(
                TaymayApplication,
                IS_PREMIUM,
                true
            )
            showDialogUpgrade(clasHome)
        }

        override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
            TaymayApplication.taymayLogString("onProductRestored-dialog", purchaseInfo.originalJson)
            if (purchaseInfo.sku in nonCons)
                MyCache.putBooleanValueByName(
                    TaymayApplication,
                    IS_PREMIUM,
                    true
                )
            showDialogUpgrade(clasHome)
        }

        override fun onPurchaseFailed(
            purchaseInfo: DataWrappers.PurchaseInfo?,
            billingResponseCode: Int?
        ) {
        }
    }

    fun loadPrice(nonCons: List<String>, product: String, tvPrice: TextView) {
        val cons: List<String> = ArrayList()
        val subs: List<String> = ArrayList()
        iapConnector = IapConnector(context, nonCons, cons, subs)
        val mPurchaseServiceListener: PurchaseServiceListener = object : PurchaseServiceListener {


            override fun onPricesUpdated(iapKeyPrices: Map<String, List<DataWrappers.ProductDetails>>) {
                if (iapKeyPrices.containsKey(product))
                    tvPrice.setText(iapKeyPrices[product]?.first()?.price + " / Lifetime") else tvPrice.setText(
                    ""
                )
                iapConnector.removePurchaseListener(this)
            }

            override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {}
            override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo) {}
            override fun onPurchaseFailed(
                purchaseInfo: DataWrappers.PurchaseInfo?,
                billingResponseCode: Int?
            ) {

            }
        }
        iapConnector.addPurchaseListener(mPurchaseServiceListener)
    }


    fun showDialogRemoveAd(idProducts: String, clasHome: Class<*>) {
        this.product = taymayGetDataString("iap", idProducts).split(",").map { it.trim() }[0]
        this.clasHome = clasHome
        val cons: List<String> = ArrayList()
        val subs: List<String> = ArrayList()
        this.nonCons = taymayGetDataString("iap", idProducts).split(",").map { it.trim() }
        iapConnector = IapConnector(context, nonCons, cons, subs)
        dialogIAP = Dialog(context)
        dialogRemoveAdsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_remove_ads, null, false
        )
        dialogRemoveAdsBinding.llDialogRemoveAd.visibility = View.GONE
        dialogRemoveAdsBinding.llLoading.visibility = View.VISIBLE
        dialogIAP.setContentView(dialogRemoveAdsBinding.getRoot())
        try {
            dialogIAP.show()
        } catch (e: Exception) {
        }
        dialogRemoveAdsBinding.icClose.setOnClickListener { v: View? ->
            if (dialogIAP.isShowing) {
                dialogIAP.dismiss()
            }
        }
        iapConnector.addPurchaseListener(purchaseServiceListener)
    }

    fun showDialogUpgrade(classHome: Class<*>?) {
        dialogUpgradedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_upgraded, null, false
        )
        var dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.setContentView(dialogUpgradedBinding.getRoot())
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                dialog.dismiss()

                if (purchaseServiceListener != null && iapConnector != null) iapConnector.removePurchaseListener(
                    purchaseServiceListener
                )
                val restartApp = Intent(context, classHome)
                restartApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(restartApp)
            }
        }.start()
        if (dialogIAP != null && dialogIAP.isShowing) dialogIAP.dismiss()
        try {
            dialog.show()
        } catch (e: BadTokenException) {
            if (purchaseServiceListener != null && iapConnector != null) iapConnector.removePurchaseListener(
                purchaseServiceListener
            )
            val restartApp = Intent(context, classHome)
            restartApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(restartApp)
        }
    }


}