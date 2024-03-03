package app.module.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import app.module.R;


public class MyFeedback {
    public static void show(Activity activity, OnFeedback onFeedback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = View.inflate(activity, R.layout.dialog_my_feedback, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        customLayout
                .findViewById(R.id.btn_cancel_feed)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
        customLayout
                .findViewById(R.id.btn_submit_feed)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText editText = customLayout.findViewById(R.id.txt_feedback);
                                String txt = editText.getText().toString();
                                if (!txt.isEmpty()) {
                                    // Analytics.Log(context, "feedback", txt);
//                  taymayApplication.LogFirebase("feedback", txt);
                                    onFeedback.onFeed(txt);
                                    //                    Cảm ơn sự đóng góp của bạn! chúng tôi sẽ cố gắng để hoàn
                                    // thiện sản phẩm giúp bạn có trải nghiệm tốt hơn!
                                    Toast.makeText(activity, "Thank you for your feedback!", Toast.LENGTH_SHORT)
                                            .show();
                                }
                                dialog.dismiss();
                            }
                        });
        dialog.show();
    }

    public static void OpenPolicy(Context context, String policy) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(policy));
        context.startActivity(i);
    }

    public interface OnFeedback {
        void onFeed(String feedback);
    }
}
