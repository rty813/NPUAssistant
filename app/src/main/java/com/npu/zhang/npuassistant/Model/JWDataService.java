package com.npu.zhang.npuassistant.Model;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.npu.zhang.npuassistant.CardViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.npu.zhang.npuassistant.CardViewActivity.myCookie;
import static com.npu.zhang.npuassistant.UrlCollection.*;

public class JWDataService extends Service {
    private CookieManager cookieManager;
    private OkHttpClient client;
    private Intent msgIntent;
    private int finishCount = 0;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            finishCount++;
            if (finishCount == 2){
                sendBroadcast(msgIntent);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        if (intent.getStringExtra("username") != null){
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
        }
        else{
            msgIntent = new Intent("com.npu.zhang.npuassistant.finishJWDataGet");
            getExceciseData();
            getGradingData();
            getPaperTestData();
        }
        return null;
    }

    @Nullable
    private void getExceciseData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(EXERCISE_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Cookie", CardViewActivity.myCookie.getAXCookie());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                    reader.close();
                    String exerciseData = builder.toString();


                    JSONObject jsonObject = new JSONObject(exerciseData);
                    JSONObject data = (JSONObject) jsonObject.getJSONArray("Result").get(0);
                    String exercise_num = data.getString("NUM");
//                    String MINUTES = data.getString("MINUTES");
//                    String KM = data.getString("MINUTES");
//                    String CAL = data.getString("MINUTES");
//                    String TODAYNUM = data.getString("MINUTES");
                    String exercise_pe = data.getString("PE");
                    String exercise_teacher = data.getString("TEACHER");
                    Intent intent = new Intent("com.npu.zhang.npuassistant.EXERCISE_UPDATE");
                    intent.putExtra("exercise_num", "跑操次数：" + exercise_num);
                    intent.putExtra("exercise_pe", exercise_pe);
                    intent.putExtra("exercise_teacher", exercise_teacher);
                    sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void getGradingData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void getPaperTestData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PAPERTEST_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Cookie", CardViewActivity.myCookie.getJWCookie());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                    reader.close();
                    String paperTestData = builder.toString();
                    System.out.println(paperTestData);

                    msgIntent.putExtra("testName", "空气动力学");
                    msgIntent.putExtra("testLocation", "教学西楼D座301");
                    msgIntent.putExtra("testDate", "2017年6月15日");
                    msgIntent.putExtra("testTime", "21点56分");
                    handler.sendEmptyMessage(1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
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
