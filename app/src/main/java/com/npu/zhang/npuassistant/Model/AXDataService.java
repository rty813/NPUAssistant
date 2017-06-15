package com.npu.zhang.npuassistant.Model;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.npu.zhang.npuassistant.CardViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.npu.zhang.npuassistant.UrlCollection.*;

public class AXDataService extends Service {
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
        msgIntent = new Intent("com.npu.zhang.npuassistant.finishAXDataGet");
        getCardData();
        getBookData();
        return null;
    }

    @Nullable
    private String[] getCardData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(CARDDETAIL_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Cookie", CardViewActivity.myCookie.getAXCookie());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                    reader.close();
                    String cardData = builder.toString();
//                    System.out.println("CARDDATA:::::::");
//                    System.out.println(cardData);
                    JSONObject jsonObject = new JSONObject(cardData);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray list = data.getJSONArray("list");
                    JSONObject recentRecord = list.getJSONObject(0);
                    String blance = recentRecord.getString("blance");
                    String payment = recentRecord.getString("payment");
                    String occurrenceTime = recentRecord.getString("occurrenceTime");
                    String operationTitle = recentRecord.getString("operationTitle");
                    msgIntent.putExtra("cardblance", blance);
                    msgIntent.putExtra("payment", payment);
                    msgIntent.putExtra("occurrenceTime", occurrenceTime);
                    msgIntent.putExtra("operationTitle", operationTitle);
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return null;
    }

    @Nullable
    private String[] getBookData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(BOOK_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Cookie", CardViewActivity.myCookie.getAXCookie());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                    reader.close();
                    String bookData = builder.toString();

                    JSONObject jsonObject = new JSONObject(bookData);
                    JSONObject data = jsonObject.getJSONObject("data");
                    String circsCount = data.getString("circsCount");
                    String debt = data.getString("debt");
                    String minDueDayBookDate = data.getString("minDueDayBookDate");
                    String expiredBookCount = data.getString("expiredBookCount");
                    msgIntent.putExtra("circsCount", circsCount);
                    msgIntent.putExtra("debt", debt);
                    msgIntent.putExtra("minDueDayBookDate", minDueDayBookDate);
                    msgIntent.putExtra("expiredBookCount", expiredBookCount);
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return null;
    }
}
