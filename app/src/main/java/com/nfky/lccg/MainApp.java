package com.nfky.lccg;

import android.app.Application;

import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by David on 8/29/17.
 */

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        QbSdk.initX5Environment(this, null);
    }
}
