package app.module.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

import app.module.R;

public class DialogUtils {

    public static void showDialogGoToStore(Activity activity, String idApp, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(content);
        builder.setPositiveButton(
                "Go to Store",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent browserIntent =
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=" + idApp));
                        activity.startActivity(browserIntent);
                    }
                });
        builder.setNegativeButton(
                "Later",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialogShow = builder.create();
        dialogShow.setOnShowListener(
                new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface d) {
                        dialogShow
                                .getButton(AlertDialog.BUTTON_NEGATIVE)
                                .setTextColor(activity.getResources().getColor(R.color.colorLater));
                        dialogShow
                                .getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(activity.getResources().getColor(R.color.colorGoTo));
                        dialogShow.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
                        dialogShow.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
                    }
                });
        dialogShow.show();
    }
}
