package app.module.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import app.module.R
import app.module.databinding.LayoutDialogRateAndFeedbackBinding
import app.module.utils.MyCache
import app.module.utils.taymayLog

class DialogRateAndReview {

    lateinit var layoutDialogRateAndFeedbackBinding: LayoutDialogRateAndFeedbackBinding

//    fun askRateAndFeedback(context: Context, onDone: () -> Unit) {
//        if (MyCache.getBooleanValueByName(context, IS_RATED, false) || IS_SHOW_ONCE) {
//
//            onDone()

//            return

//        }
//        IS_SHOW_ONCE = true
//        showDialogRateAndFeedback(context, onDone)
//    }

    fun askRateAndFeedbackNextSession(context: Context, onDone: () -> Unit) {
        var isOpenApp = MyCache.getBooleanValueByName(context, "is_open_app")
        if (!isOpenApp) {
            MyCache.putBooleanValueByName(context, "is_open_app", true)

            onDone()
            return
        }
        if (MyCache.getBooleanValueByName(context, IS_RATED, false) || IS_SHOW_ONCE) {
            onDone()
            return
        }
        IS_SHOW_ONCE = true
        showDialogRateAndFeedback(context, onDone)
    }

    fun showDialogRateAndFeedback(context: Context, onDone: () -> Unit) {
        layoutDialogRateAndFeedbackBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_dialog_rate_and_feedback,
            null, false
        )
        layoutDialogRateAndFeedbackBinding.viewRateContent.visibility = View.VISIBLE
        layoutDialogRateAndFeedbackBinding.edtFeedback.visibility = View.GONE
        layoutDialogRateAndFeedbackBinding.viewThanksContent.visibility = View.GONE
        layoutDialogRateAndFeedbackBinding.imvEmojiSelected.visibility = View.GONE
        layoutDialogRateAndFeedbackBinding.tvEmojiSelected.visibility = View.GONE
        layoutDialogRateAndFeedbackBinding.tvRateSubmit.visibility = View.GONE
        val emojis = intArrayOf(
            R.drawable.ic_emoji_1,
            R.drawable.ic_emoji_2,
            R.drawable.ic_emoji_3,
            R.drawable.ic_emoji_4,
            R.drawable.ic_emoji_5
        )
        val emoji_selects = intArrayOf(
            R.drawable.ic_emoji_selected_1,
            R.drawable.ic_emoji_selected_2,
            R.drawable.ic_emoji_selected_3,
            R.drawable.ic_emoji_selected_4,
            R.drawable.ic_emoji_selected_5
        )
        val rate_text = arrayOf("Terrible", "Bad", "Okay", "Good!", "Great!")
        val txtSubmits =
            arrayOf("Send", "Send", "Send", "Rate us on Google Play", "Rate us on Google Play")
        val emoji_grays = intArrayOf(
            R.drawable.ic_emoji_gray_1,
            R.drawable.ic_emoji_gray_2,
            R.drawable.ic_emoji_gray_3,
            R.drawable.ic_emoji_gray_4,
            R.drawable.ic_emoji_gray_5
        )
        val rate_bars = arrayOf(
            layoutDialogRateAndFeedbackBinding.rate1,
            layoutDialogRateAndFeedbackBinding.rate2,
            layoutDialogRateAndFeedbackBinding.rate3,
            layoutDialogRateAndFeedbackBinding.rate4,
            layoutDialogRateAndFeedbackBinding.rate5
        )
        var dialog = Dialog(context);
        dialog.setContentView(layoutDialogRateAndFeedbackBinding.getRoot())
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        //   eventLog(context, "rate_open");
        dialog.show()
        for (imageView in rate_bars) {
            imageView.setOnClickListener {
                val rate: Int = imageView.tag.toString().toInt()


                // update rate icon
                for (i in emoji_grays.indices) rate_bars[i].setImageResource(emoji_grays[i])
                for (i in 0 until rate) {
                    rate_bars[i].setImageResource(emojis[i])
                    layoutDialogRateAndFeedbackBinding.imvEmojiSelected.setImageResource(
                        emoji_selects[i]
                    )
                    layoutDialogRateAndFeedbackBinding.tvEmojiSelected.text = rate_text[i]
                    layoutDialogRateAndFeedbackBinding.tvRateSubmit.text = txtSubmits[i]
                }
                layoutDialogRateAndFeedbackBinding.imvEmojiSelected.visibility = View.VISIBLE
                layoutDialogRateAndFeedbackBinding.tvEmojiSelected.visibility = View.VISIBLE
                layoutDialogRateAndFeedbackBinding.tvRateSubmit.visibility = View.VISIBLE
                when (rate) {
                    1, 2, 3 -> {
                        layoutDialogRateAndFeedbackBinding.edtFeedback.visibility = View.VISIBLE
                        layoutDialogRateAndFeedbackBinding.tvTitleFeedback.text =
                            "What is\nsomething we can improve?"
                        layoutDialogRateAndFeedbackBinding.tvRateSubmit.setOnClickListener { //   eventLog(context, "rate_tap_feedback");
                            //   eventLog(context, "rate_rate_"+rate);
                            val txt_feedback =
                                layoutDialogRateAndFeedbackBinding.edtFeedback.text.toString()
                            if (!txt_feedback.isEmpty()) context.taymayLog(
                                "feedback", txt_feedback
                            )
//                           Log(adApp, "feedback", txt_feedback);
                            layoutDialogRateAndFeedbackBinding.viewThanksContent.visibility =
                                View.VISIBLE
                            layoutDialogRateAndFeedbackBinding.viewRateContent.visibility =
                                View.GONE
                            object : CountDownTimer(1500, 1000) {
                                override fun onTick(millisUntilFinished: Long) {}
                                override fun onFinish() {
                                    if (dialog.isShowing) dialog.dismiss()
                                    onDone()
                                }
                            }.start()
                        }
                    }

                    4, 5 -> {
                        layoutDialogRateAndFeedbackBinding.edtFeedback.visibility = View.GONE
                        layoutDialogRateAndFeedbackBinding.tvTitleFeedback.text =
                            "How was your\nexperience with us?"
                        layoutDialogRateAndFeedbackBinding.tvRateSubmit.setOnClickListener { //   eventLog(context, "rate_tap_rate");
                            //   eventLog(context, "rate_rate_"+rate);
                            // adApplication.TrackingFirebase("rate_rate_app");
                            MyCache.putBooleanValueByName(context, IS_RATED, true)
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "https://play.google.com/store/apps/details?id="
                                            + context.getPackageName()
                                )
                            )
                            context.startActivity(browserIntent)
                            if (dialog.isShowing) dialog.dismiss()
                            onDone()
                        }
                    }
                }
            }
        }
        dialog.setOnDismissListener { // adApplication.TrackingFirebase("rate_close");
            onDone()
        }
    }

    fun showDialogFeedback(context: Context, onDone: () -> Unit) {
        val myActivity = context as Activity
        layoutDialogRateAndFeedbackBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.layout_dialog_rate_and_feedback, null, false
        )
        layoutDialogRateAndFeedbackBinding.viewRateContent.visibility = View.VISIBLE
        layoutDialogRateAndFeedbackBinding.viewThanksContent.visibility = View.GONE
        layoutDialogRateAndFeedbackBinding.viewRateBar.visibility = View.GONE
        layoutDialogRateAndFeedbackBinding.imvEmojiSelected.visibility = View.GONE
        layoutDialogRateAndFeedbackBinding.tvEmojiSelected.visibility = View.GONE
        layoutDialogRateAndFeedbackBinding.tvRateSubmit.visibility = View.GONE
        var dialog = Dialog(context)
        dialog.setContentView(layoutDialogRateAndFeedbackBinding.getRoot())
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        layoutDialogRateAndFeedbackBinding.tvRateSubmit.visibility = View.VISIBLE
        layoutDialogRateAndFeedbackBinding.edtFeedback.visibility = View.VISIBLE
        layoutDialogRateAndFeedbackBinding.tvTitleFeedback.text =
            "What is\nsomething we can improve?"
        layoutDialogRateAndFeedbackBinding.tvRateSubmit.setOnClickListener {
            val txt_feedback = layoutDialogRateAndFeedbackBinding.edtFeedback.text.toString()
            //            if (!txt_feedback.isEmpty())
//             Log(adApp, "feedback", txt_feedback);
            layoutDialogRateAndFeedbackBinding.viewThanksContent.visibility = View.VISIBLE
            layoutDialogRateAndFeedbackBinding.viewRateContent.visibility = View.GONE
            object : CountDownTimer(1500, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    if (dialog.isShowing) dialog.dismiss()
                    onDone()
                }
            }.start()
        }
        dialog.setOnDismissListener { // adApplication.TrackingFirebase("rate_close");
            onDone()
        }
    }

    companion object {
        private const val IS_RATED = "IS_RATED"
        var IS_SHOW_ONCE = false
        fun isRated(context: Context?): Boolean {
            return MyCache.getBooleanValueByName(context, IS_RATED, false)
        }
    }
}