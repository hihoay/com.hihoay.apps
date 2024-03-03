package app.module.utils

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import app.module.R
import java.util.Random

var REQUEST_POST_NOTIFICATIONS_CODE = 111
var NOTIFICATION_ID = "appName_notification_id"
var NOTIFICATION_NAME = "appName"
var NOTIFICATION_CHANNEL = "appName_channel_01"
fun isGrantNotifyPermistion(context: Context, permission: (isGrant: Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permission(true)
        } else permission(false)
    } else
        permission(true)

}

fun askNotifyPermistion(context: Context, permission: (isGrant: Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permission(true)
        } else (context as Activity).requestPermissions(
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            REQUEST_POST_NOTIFICATIONS_CODE
        )
    } else {
        permission(true)
    }
}


//override fun onRequestPermissionsResult(
//    requestCode: Int,
//    permissions: Array<out String>,
//    grantResults: IntArray
//) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        elog(
//            "onRequestPermissionsResult", requestCode, ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//
//        val intent = Intent()
//        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//        val uri = Uri.fromParts("package", this.packageName, null)
//        intent.data = uri
//        startActivityForResult(intent, REQUEST_POST_NOTIFICATIONS_CODE)
//    }
//}

fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun notify(
    context: Context,
    intent: Intent,
    titleNotification: String,
    subtitleNotification: String,
    icon: Int, vararg actions: NotificationCompat.Action
) {
    NOTIFICATION_CHANNEL = context.packageName
    NOTIFICATION_ID = context.packageName
//    val intent = Intent(applicationContext, MainActivity::class.java)
//    val intent = Intent("notify")
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    intent.putExtra(NOTIFICATION_ID, Random().nextInt(10000))

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val bitmap = context.vectorToBitmap(icon)
    val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
        .setLargeIcon(bitmap).setSmallIcon(icon)
        .setContentTitle(titleNotification).setContentText(subtitleNotification)
        .setDefaults(NotificationCompat.DEFAULT_ALL).setContentIntent(pendingIntent)
        .setAutoCancel(true)

    actions.forEach {
        notification.addAction(it)
    }

    notification.priority = NotificationCompat.PRIORITY_MAX

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notification.setChannelId(NOTIFICATION_CHANNEL)
        val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL,
                context.resources.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        channel.setSound(ringtoneManager, audioAttributes)
        notificationManager.createNotificationChannel(channel)
    }

    notificationManager.notify(Random().nextInt(10000), notification.build())
}

