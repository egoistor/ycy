package com.example.ycy;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

public class MyLeanCloudApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"rgLItFwcTFaSuvDEQslm7Hsv-gzGzoHsz","5eunkQ2uQGHpynHJ50cNKXGR");
        AVOSCloud.setDebugLogEnabled(true);
    }

}
