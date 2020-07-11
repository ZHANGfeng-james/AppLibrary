package com.ant.codedesignpatterns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView mIvShow;
    private ImageLoader mImageLoader;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private int REQUEST_PERMISSION_CODE = 0xFF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIvShow = MainActivity.this.findViewById(R.id.img_show);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "启动权限申请流程！");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            } else {
                Log.d(TAG, "已获得权限");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean permissionsGrant = true;
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);

                permissionsGrant = (PackageManager.PERMISSION_GRANTED == grantResults[i]) ? true : false;
            }

            if (permissionsGrant) {

            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        mImageLoader = new ImageLoader();
        mImageLoader.useDoubleCache(true);

        startLoadImage();
    }

    private void startLoadImage() {
        final String imageUrl = "https://www.mobibrw.com/wp-content/uploads/2018/12/NDK_ABI_CHECKS-768x469.png";
        mImageLoader.displayImage(MainActivity.this, imageUrl, mIvShow);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageLoader.displayImage(MainActivity.this, imageUrl, mIvShow);
                    }
                });
            }
        }).start();
    }
}
