package com.ant.codedesignpatterns;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();

    private final ExecutorService mExecutorService;
    private final ImageCache mImageCache;

    private final DiskCache mDiskCache;
    private boolean isUseDiskCache = false;

    public ImageLoader() {
        mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        mImageCache = new ImageCache();

        mDiskCache = new DiskCache();
    }

    private Bitmap downloadImage(String imageUrl) {
        Bitmap bitmap = null;

        try {
            URL url = new URL(imageUrl);

            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());

            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public void displayImage(final Activity activity, final String url, final ImageView imageView) {
        Bitmap bitmap = isUseDiskCache ? mDiskCache.get("1.png") : mImageCache.getCache(url);
        if (bitmap != null) {
            Log.d(TAG, "ImageCache is cached!");
            // ImageCache 已缓存，直接获取并填充 ImageView
            imageView.setImageBitmap(bitmap);
            return;
        }

        imageView.setTag(url);

        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = downloadImage(url);
                if (bitmap == null) {
                    Log.d(TAG, "bitmap is null!");
                    return;
                }

                Log.d(TAG, "bitmap: width:" + bitmap.getWidth() + "; height:" + bitmap.getHeight());
                if (imageView.getTag().equals(url)) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }

                if (isUseDiskCache) {
                    // workThread
                    mDiskCache.put("1.png", bitmap);
                } else {
                    mImageCache.setImageToCache(url, bitmap);
                }
            }
        });
    }

    public void useDiskCache(boolean useDiskCache) {
        this.isUseDiskCache = useDiskCache;
    }
}
