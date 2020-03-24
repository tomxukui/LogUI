package com.xukui.demo.logui;

import android.app.Application;

import com.xukui.library.logui.LogUI;

import java.io.File;

public class Mapp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUI.init(true, getCacheDir("logs").getAbsolutePath());
    }

    /**
     * 获取缓存目录
     */
    private File getCacheDir(String type) {
        File dir = getExternalCacheDir();

        if (dir != null) {
            dir = new File(dir, type);

            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        return dir;
    }

}
