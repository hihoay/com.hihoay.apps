package app.module.lang

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

const val APP_REQ_PERMISTION: Int = 121

fun Context.askPermisstion(actitivty: Activity, callback: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            showDialogConfirmRequest(actitivty) {
                kotlin.run {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        actitivty.startActivityForResult(intent, APP_REQ_PERMISTION)
                    } catch (e: Exception) {
                        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        intent.addCategory("android.intent.category.DEFAULT")
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        actitivty.startActivityForResult(intent, APP_REQ_PERMISTION)
                    }

                }
            }
        } else
            callback(true)
    } else if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        callback(true)
    } else Dexter.withContext(this).withPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE,
//        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ).withListener(object : MultiplePermissionsListener {
        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
            if (p0!!.areAllPermissionsGranted())
                callback(true)
            else
                showDialogConfirmRequest(actitivty) {
                    askPermisstion(actitivty, callback)
                }
        }

        override fun onPermissionRationaleShouldBeShown(
            p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
            p1: PermissionToken?
        ) {
            p1?.continuePermissionRequest()
        }


    }).check()

}

fun Context.CheckAppPermistionGranted(callback: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            callback(false)
        } else
            callback(true)
    } else if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        callback(true)
    } else callback(false)

}

fun showDialogConfirmRequest(actitivty: Activity, callback: () -> Unit) {
    android.app.AlertDialog.Builder(actitivty)
        .setTitle("App Permissions")
        .setMessage(
            "* The application supports you to read the files most popular formats today:\n\n" +
                    "- Printing documents like PDF, EPUB, PNG, JPG, BMP, TIFF, GIF, SVG, CBZ, CBR, XPS...\n" +
                    "- Text documents like DOC, DOCX, TXT, XML, HTML...\n" +
                    "- Reporting documents like XLS, XLSX, ODS, CSV, ...\n" +
                    "- Presentation documents like PPT, PPTX, ODP,..." +
                    "\n\n-> So we need to use the Storage Management permission to perform the application's features."
        )
        .setCancelable(false)
        .setPositiveButton("Yes") { dialog, id ->
            dialog.dismiss(); callback()
        }.create().show()
    //Debug.elog("show", "showDialogConfirmRequest")

}
