package com.npu.zhang.npuassistant;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkView;

import java.util.HashMap;
import java.util.Map;

import static com.npu.zhang.npuassistant.UrlCollection.LOGIN_URL;

public class LoginActivity extends AppCompatActivity {

    private XWalkView xWalkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String cookie = intent.getStringExtra("cookie");

        xWalkView = (XWalkView) findViewById(R.id.xwalkView);
        XWalkCookieManager manager = new XWalkCookieManager();
        manager.setAcceptCookie(true);
        manager.setCookie(url, cookie);

        System.out.println(manager.getCookie(url));

        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie);
        xWalkView.loadUrl(url);
//        webView.loadUrl(url);
    }

    public static void synCookies(Context context, String url, String cookie) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(url, cookie);//cookies是在HttpClient中获得的cookie
        System.out.println(url + "\r\n" + cookie);
        CookieSyncManager.getInstance().sync();
    }

    /**通过Activity管理XWalkWebView的声明周期*/
    @Override protected void onPause() {
        super.onPause();
        if (xWalkView != null) {
            xWalkView.pauseTimers();
            xWalkView.onHide();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (xWalkView != null) {
            xWalkView.resumeTimers();
            xWalkView.onShow();
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (xWalkView != null) {
            xWalkView.onDestroy();
        }
    }
}
