package com.example.solutionsproject.classes.imagetools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class GetFileNameFromUri {
    @SuppressLint("Range")
    public static String getFileNameFromUri (Context context, Uri uri) {
        @SuppressLint("Recycle")
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);

        assert c != null;
        c.moveToFirst();

        String fileName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        c.close();

        return fileName;
    }
}
