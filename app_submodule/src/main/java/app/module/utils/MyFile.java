package app.module.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class MyFile {
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(File filePath) throws Exception {
        FileInputStream fin = new FileInputStream(filePath);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    public static String getStringFromFile(File filePath, String def) {
        try {
            FileInputStream fin = new FileInputStream(filePath);
            String ret = convertStreamToString(fin);
            fin.close();
            return ret;
        } catch (Exception e) {
        }
        return def;
    }

    public static String getJsonFromRawFile(Context context, int raw_file) {
        String jsonString = "";
        try {
            InputStream is = context.getResources().openRawResource(raw_file);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            jsonString = writer.toString();
        } catch (Exception e) {
        }

        return jsonString;
    }

    public static String getJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(context.getPackageName() + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            // MyPrinter.econtext.getClass(),context.getPackageName()+".json");

            return null;
        }

        return json;
    }

    public static String getJSONFromAsset(Context context, String file_name, String def) {
        String json = "";
        try {
            InputStream is = context.getAssets().open(file_name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return def;
        }
        return json;
    }

    public static String getJSONFromAsset(Context context, String file_name) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(file_name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            // MyPrinter.econtext.getClass(),context.getPackageName()+".json");

            return null;
        }

        return json;
    }

    public static String loadJSONCrossFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("my-cross.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static void writeToFile(String data, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            // ////Debug.elog("Exception", "File write failed: " + e.toString());
        }
    }
}
