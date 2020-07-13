package com.ant.codedesignpatterns;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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

    private ImageCache mImageCache;

    public ImageLoader() {
        mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        // 默认使用的是内存缓存器 MemeoryCache
        mImageCache = new MemoryCache();
    }

    public void setImageCache(ImageCache imageCache) {
        this.mImageCache = imageCache;
    }

    public void displayImage(final Activity activity, final String url, final ImageView imageView) {
        Bitmap bitmap = mImageCache.get(url);

        if (bitmap != null) {
            Log.d(TAG, "已从缓存中取得对象");
            // 已从缓存中找到 Bitmap 对象
            imageView.setImageBitmap(bitmap);
            return;
        }
        submitLoadRequest(activity, url, imageView);
    }

    private void submitLoadRequest(final Activity activity, final String url, final ImageView imageView) {
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

                mImageCache.put(url, bitmap);
            }
        });
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
}
