package app.module.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;

import com.caverock.androidsvg.SVG;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyConnection {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static boolean taymayIsOnline(Context mContext) {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        return new OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS);
    }

    private static OkHttpClient getNewHttpClient() {
        OkHttpClient.Builder client =
                new OkHttpClient.Builder()
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .retryOnConnectionFailure(true)
                        .cache(null)
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS);

        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            return enableTls12OnPreLollipop(client).build();

        } else return client.build();
    }

    public static String taymayGetBodyByUrl(String url) throws IOException {
        //        OkHttpClient client = new OkHttpClient.Builder()
        //                .connectTimeout(180, TimeUnit.SECONDS)
        //                .writeTimeout(180, TimeUnit.SECONDS)
        //                .readTimeout(180, TimeUnit.SECONDS)
        //                .build();
        Request request = new Request.Builder().url(url).build();
        Response response = getNewHttpClient().newCall(request).execute();
        return response.body().string();
    }

    static String taymayPostJsonToUrl(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, JSON); // new
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static void urlSVGToFile(Context context, String url, int size, OnDone onDone) {
        new AsyncTask<Void, Void, File>() {
            @Override
            protected File doInBackground(Void... voids) {
                File file = null;
                SVG svg = null;
                //////elog("urlSVGToFile", url);
                file = new File(context.getExternalCacheDir() + "/cache.png");
                try {
                    String body = MyConnection.taymayGetBodyByUrl(url);
                    svg = SVG.getFromString(body);
                    Bitmap newBM = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    Canvas bmcanvas = new Canvas(newBM);
                    bmcanvas.drawRGB(255, 255, 255);
                    svg.renderToPicture(size, size).draw(bmcanvas);
                    newBM.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                    return file;
                } catch (Exception e) {
                    ////elog("urlSVGToFile", e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(File file) {
                onDone.onDone(file);
            }
        }.execute();
    }

    public interface OnDone {
        void onDone(File file);
    }
}
