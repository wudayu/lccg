package com.nfky.lccg;

import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static String URL = "http://hxapp.udplat.com/";
    WebView wv = null;
    TextView tvTitle = null;
    View ivBack = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wv = (WebView) findViewById(R.id.wv_main);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initWidgets();
        initCookie();
        initWebView();
    }

    void initWidgets() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKeyDown(KeyEvent.KEYCODE_BACK, null);
            }
        });
    }

    void initCookie() {
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
    }

    void initWebView() {
        WebSettings webSettings = wv.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(true);
        // 为图片添加放大缩小功能
        webSettings.setUseWideViewPort(true);

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                if (Build.VERSION.SDK_INT < 21) {
                    CookieSyncManager.getInstance().sync();
                } else {
                    CookieManager.getInstance().flush();
                }

                super.onPageFinished(webView, s);
            }
        });
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String title) {
                super.onReceivedTitle(webView, title);

                tvTitle.setText(title);
            }
        });

        wv.loadUrl(URL);
    }

    @Override
    protected void onPause() {
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onPressBack())
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    boolean onPressBack() {
        if (wv.canGoBack() && !wv.getUrl().equals(URL)){
            wv.goBack();
            return true;
        }

        if (!wv.canGoBack() && !wv.getUrl().contains("login")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("即将退出，是否保留登录信息？");
            builder.setPositiveButton("保留", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                }
            });
            builder.setNegativeButton("不保留", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 清除登陆Cookie
                    CookieManager.getInstance().removeSessionCookie();
                    MainActivity.this.finish();
                }
            });
            builder.show();
        }

        return false;
    }

}
