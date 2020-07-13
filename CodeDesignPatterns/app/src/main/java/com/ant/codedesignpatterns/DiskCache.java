package com.ant.codedesignpatterns;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiskCache implements ImageCache {
    private static final String TAG = DiskCache.class.getSimpleName();
    private static final String CACHE_DIR_PATH = "sdcard/cache";

    @Override
    public void put(String url, Bitmap bitmap) {
        FileOutputStream fileOutputStream = null;
        boolean outputResult = false;
        try {
            File cacheDir = new File(CACHE_DIR_PATH);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            File destFile = new File(cacheDir + File.separator + url);
            if (!destFile.exists()) {
                destFile.createNewFile();
            }

            fileOutputStream = new FileOutputStream(destFile);
            // workThread
            outputResult = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            Log.d(TAG, "compress result:" + outputResult);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Bitmap get(String url) {
        return BitmapFactory.decodeFile(CACHE_DIR_PATH + File.separator + url);
    }
}
