package app.module.activities

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import app.module.R
import app.module.databinding.LayoutCouponCodeBinding
import app.module.objecs.taymayPostJsonToUrlByKtor
import app.module.utils.IS_PREMIUM
import app.module.utils.MyCache
import app.module.utils.TaymayApplication
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class DialogCouponsCode {

    lateinit var bidding: LayoutCouponCodeBinding

    data class CodeRequest(var code: String)
    data class Result(var isOK: Boolean)

    fun showAsk(context: Context, classHome: Class<*>?) {
        bidding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.layout_coupon_code, null, false
        )
        bidding.llBtn.visibility = View.VISIBLE
        bidding.prgLoading.visibility = View.GONE
        var builder = AlertDialog.Builder(context)

        builder.setView(bidding.root)
        var dialog = builder.create()
        dialog.show()

        bidding.tvCancel.setOnClickListener {
            if (dialog.isShowing) dialog.dismiss()
        }
        bidding.tvApply.setOnClickListener {
            bidding.llBtn.visibility = View.GONE
            bidding.prgLoading.visibility = View.VISIBLE
            taymayPostJsonToUrlByKtor(
                "https://bot.taymay.io/coupon",
                Gson().toJson(CodeRequest(bidding.txtCode.text.toString())),
                Gson().toJson(Result(false))
            ) {
                MainScope().launch {
                    if (Gson().fromJson(it, Result::class.java).isOK) {
                        MyCache.putBooleanValueByName(
                            TaymayApplication,
                            IS_PREMIUM,
                            true
                        )
                        DialogRemoveAd(context as Activity).showDialogUpgrade(classHome)

                    } else
                        Toast.makeText(
                            context,
                            "Sorry! Your code has expired or is incorrect.",
                            Toast.LENGTH_SHORT
                        ).show()
                    if (dialog.isShowing) dialog.dismiss()
                    bidding.llBtn.visibility = View.VISIBLE
                    bidding.prgLoading.visibility = View.GONE
                }
            }
        }
    }
}