package app.module.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
    public static File saveImageToGallery(@NonNull File file, @NonNull Bitmap bmp) {
        if (bmp == null) {
            throw new IllegalArgumentException("bmp should not be null");
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static Bitmap convertTo169(Bitmap bm_src) {
        int w_src = bm_src.getWidth();
        int h_src = bm_src.getHeight();
        int w_out = 0;
        int h_out = 0;
        if (w_src > h_src && 16 / 9 > w_src / h_src) {
            w_out = w_src;
            h_out = 9 * w_src / 16;
        } else {
            h_out = h_src;
            w_out = 16 * h_src / 9;
        }
        Bitmap bm_out = Bitmap.createBitmap(w_out, h_out, Bitmap.Config.ARGB_8888);
        // ////Debug.elog(
        //        "kết quả",
        //        w_src + ":" + h_src,
        //        w_out + ":" + h_out,
        //        (w_out - w_src) / 2,
        //        h_out - h_src / 2);
        if (w_src > h_src && 16 / 9 > w_src / h_src) {
            new Canvas(bm_out).drawBitmap(bm_src, 0, (float) (h_out - h_src) / 2, new Paint());
        } else {
            new Canvas(bm_out).drawBitmap(bm_src, (float) (w_out - w_src) / 2, 0, new Paint());
        }
        return bm_out;
    }

    public static void notifySystemGallery(@NonNull Context context, @NonNull File file) {
        // ////Debug.elog("notifySystemGallery", file.getAbsolutePath());
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("bmp should not be null");
        }

        try {
            MediaStore.Images.Media.insertImage(
                    context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File couldn't be found");
        }

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }
}
