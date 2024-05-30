package com.example.solutionsproject.classes.imagetools;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Objects;

public class FileUtils {
    public static File drawableToFile(Context context, int drawableId, String fileName) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        File file = new File(context.getCacheDir(), fileName);
        try {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File uriToFile(Context context, Uri uri) {
        if(uri == null) return null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            if (inputStream != null) {
                File tempFile = File.createTempFile("image_", ".png");
                FileOutputStream fos = new FileOutputStream(tempFile);
                copyStream(inputStream, fos);
                inputStream.close();

                return tempFile;
            } else {
                Log.e("FileUtils", "Input stream is null");
            }
        } catch (IOException e) {
            Log.e("FileUtils", "Error reading file from Uri: " + e.getMessage());
        }

        return null;
    }

    public static Bitmap urlToBitmap(String urlString) {
        try {
            URL url = new URL(urlString);
            return BitmapFactory.decodeStream(url.openStream());
        } catch (IOException e) {
            Log.e("FileUtils", Objects.requireNonNull(e.getMessage()));
        }

        return null;
    }

    public static File saveBitmapToFile(Bitmap bitmap, String filename, Context context) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "signatures");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(context.getCacheDir(), filename + ".png");

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            return file;
        } catch (IOException e) {
            Log.e("FileUtils", Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }

    private static void copyStream(InputStream in, OutputStream os) {
        byte[] buffer = new byte[1024];
        int read;
        try {
            while ((read = in.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        } catch (IOException e) {
            Log.e("FileUtils", Objects.requireNonNull(e.getMessage()));
        }
    }

}
