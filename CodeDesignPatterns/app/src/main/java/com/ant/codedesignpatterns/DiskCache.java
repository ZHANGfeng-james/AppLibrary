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
        writeBitmapToFileSystem(url, bitmap);
    }

    @Override
    public Bitmap get(String url) {
        return BitmapFactory.decodeFile(CACHE_DIR_PATH + File.separator + url);
    }

    private boolean writeBitmapToFileSystem(String url, Bitmap bitmap) {
        FileOutputStream fileOutputStream = null;
        boolean outputResult = false;
        try {
            File cacheDir = new File(CACHE_DIR_PATH);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            // 此处可以使用 imageUrl2MD5(url) 将图片的 url --> MD5值 作为文件名
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
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}
