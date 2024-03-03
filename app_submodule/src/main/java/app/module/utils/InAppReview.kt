package app.module.utils

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory

fun Activity.taymayAskReview() {
    val manager = ReviewManagerFactory.create(this)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            //Debug.elog("addOnCompleteListener", "isSuccessful")
            val flow = manager.launchReviewFlow(this, task.result)
            flow.addOnCompleteListener { a ->
                //Debug.elog(a.isComplete, a.isSuccessful)
            }
        } else {
            //Debug.elog("addOnCompleteListener", "false")
        }
    }
}