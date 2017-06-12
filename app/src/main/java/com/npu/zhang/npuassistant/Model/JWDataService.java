package com.npu.zhang.npuassistant.Model;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.npu.zhang.npuassistant.MainActivity;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.npu.zhang.npuassistant.DetailActivity.myCookie;
import static com.npu.zhang.npuassistant.UrlCollection.JW_LOGIN_URL;

public class JWDataService extends Service {
    private CookieManager cookieManager;
    private OkHttpClient client;

    public JWDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client = new OkHttpClient().newBuilder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        imitateLogin(username, password);
        return null;
    }

    private void imitateLogin(final String username, final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();
                Request request = new Request.Builder()
                        .url(JW_LOGIN_URL)
                        .post(body)
                        .build();
                Call call = client.newCall(request);
                try {
                    call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                List<HttpCookie> cookieList = cookieManager.getCookieStore().getCookies();
                for (int i = cookieList.size()-1; i>=0; i--){
                    if ((cookieList.get(i).toString()).contains("JSESSIONID")){
                        myCookie.setJWCookie(cookieList.get(i).toString());
                        break;
                    }
                }
                sendBroadcast(new Intent("com.npu.zhang.npuassistant.finishJWLogin"));
            }
        }).start();
    }
}
