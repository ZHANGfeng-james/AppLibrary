package com.ant.codedesignpatterns;

import android.graphics.Bitmap;
import android.util.Log;

public class DoubleCache implements ImageCache{
    private static final String TAG = DoubleCache.class.getSimpleName();

    MemoryCache mMemoryCache = new MemoryCache();
    DiskCache mDiskCache = new DiskCache();

    @Override
    public Bitmap get(String url) {
        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap == null) {
            bitmap = mDiskCache.get(url);

            if (bitmap != null) {
                Log.d(TAG, "已从 sd 卡缓存中获取到 Bitmap");
            }
        } else {
            Log.d(TAG, "已从内存缓存中获取到 Bitmap");
        }

        return bitmap;
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        // 双缓存图片也就是内存和 sdcard 中都缓存一份
        mMemoryCache.put(url, bitmap);
        mDiskCache.put(url, bitmap);
    }
}
