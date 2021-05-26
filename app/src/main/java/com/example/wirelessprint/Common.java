package com.example.wirelessprint;

import android.content.Context;
import android.util.Log;

import java.io.File;

import static android.content.ContentValues.TAG;

public class Common {

    public static String getAppPath(Context context) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory()
                + File.separator
                + context.getResources().getString(R.string.app_name)
                + File.separator
        );

        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
            if (!mkdir) {
                Log.e(TAG, "Directory creation failed.");
            }
        }
        return dir.getPath() + File.separator;
    }
}