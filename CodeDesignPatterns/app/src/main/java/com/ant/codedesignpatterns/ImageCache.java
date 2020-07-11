package com.ant.codedesignpatterns;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class ImageCache {

    private static final String TAG = ImageCache.class.getSimpleName();

    private LruCache<String, Bitmap> mImageCache;

    public ImageCache(){
        initImageCache();
    }

    private void initImageCache(){
        // TODO: 2020-07-08 计算的单位是什么？计算得到的是 KB
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 393216KB --> 384MB 内存
        Log.d(TAG, "Processor's maxMemory:"+ maxMemory);

        // 384MB --> 96MB 可用于缓存
        final int cacheSize = maxMemory / 4;
        mImageCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                int bitmapSize = bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                // 1047KB 接近 1.022 MB
                Log.d(TAG, "bitmapSize:"+ bitmapSize);

                return bitmapSize;
            }
        };
    }

    public void setImageToCache(String url, Bitmap bitmap){
        mImageCache.put(url, bitmap);
    }

    public Bitmap getCache(String url){
        return mImageCache.get(url);
    }
}
