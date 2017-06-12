package com.npu.zhang.npuassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkView;

import java.util.HashMap;
import java.util.Map;

public class WebDetailActivity extends AppCompatActivity {

    private XWalkView xWalkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String cookie = intent.getStringExtra("cookie");

        System.out.println(cookie);
        xWalkView = (XWalkView) findViewById(R.id.xwalkView);
        XWalkCookieManager manager = new XWalkCookieManager();
        manager.setAcceptCookie(true);
        manager.setCookie(url, cookie);

//        System.out.println(manager.getCookie(url));

        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie);
        xWalkView.loadUrl(url);
//        webView.loadUrl(url);
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
