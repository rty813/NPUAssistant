package com.npu.zhang.npuassistant;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static com.npu.zhang.npuassistant.UrlCollection.LOGIN_URL;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        synCookies(this, url, MainActivity.myCookie.getAXCookie());
        webView.loadUrl(url);
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
}
