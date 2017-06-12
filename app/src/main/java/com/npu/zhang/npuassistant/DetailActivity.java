package com.npu.zhang.npuassistant;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.npu.zhang.npuassistant.Model.JWDataService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.npu.zhang.npuassistant.UrlCollection.*;

public class DetailActivity extends AppCompatActivity {

    private String username;
    private String password;
    public static MyCookie myCookie;
    private AlertDialog.Builder builder;
    private ProgressDialog dialog;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.npu.zhang.npuassistant.finishJWLogin":
                    dialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);
//        findView();
//        getData();
        IntentFilter intentFilter = new IntentFilter("com.npu.zhang.npuassistant.finishJWLogin");
        registerReceiver(broadcastReceiver, intentFilter);

        dialog = new ProgressDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("正在登录教务系统");
        dialog.setCancelable(false);

        myCookie = new MyCookie();
        getTxtFileInfo();
        if (username != null){
            dialog.show();
            Intent intent = new Intent(this, JWDataService.class);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            bindService(intent, JWConnection, BIND_AUTO_CREATE);
        }
        else{
            startActivity(new Intent(this, MainActivity.class));
        }
        findViewById(R.id.cv_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
                intent.putExtra("url", CARD_WEB_URL);
                intent.putExtra("cookie", myCookie.getAXCookie());
                startActivity(intent);
            }
        });

        findViewById(R.id.cv_papertest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
                intent.putExtra("url", PAPERTEST_WEB_URL);
                intent.putExtra("cookie", myCookie.getJWCookie());
                startActivity(intent);
            }
        });
    }

    private final ServiceConnection JWConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

//    private void findView(){
//
//    }

    private void getData(){
        Intent intent = getIntent();
        String text = intent.getStringExtra("card");
        ((TextView)findViewById(R.id.tv_cardblance)).setText(getCardData(text)[0]);
        ((TextView)findViewById(R.id.tv_payment)).setText(getCardData(text)[1]);
        ((TextView)findViewById(R.id.tv_occurrenceTime)).setText(getCardData(text)[2]);
        ((TextView)findViewById(R.id.tv_operationTitle)).setText(getCardData(text)[3]);

        text = intent.getStringExtra("book");
        ((TextView)findViewById(R.id.tv_circsCount)).setText(getBookInfo(text)[0]);
        ((TextView)findViewById(R.id.tv_debt)).setText(getBookInfo(text)[1]);
        ((TextView)findViewById(R.id.tv_minDueDayBookDate)).setText(getBookInfo(text)[2]);
        ((TextView)findViewById(R.id.tv_expiredBookCount)).setText(getBookInfo(text)[3]);

//        System.out.println(intent.getStringExtra("exam"));
        String[] exerciseData = getExceciseData(intent.getStringExtra("exercise"));
        Intent intent1 = new Intent("com.npu.zhang.npuassistant.EXERCISE_UPDATE");
        intent1.putExtra("exercise_num", "跑操次数：" + exerciseData[0]);
        intent1.putExtra("exercise_pe", exerciseData[5]);
        intent1.putExtra("exercise_teacher", exerciseData[6]);
        sendBroadcast(intent1);
    }

    @Nullable
    private String[] getExceciseData(String exercise){
        try {
            JSONObject jsonObject = new JSONObject(exercise);
            JSONObject data = (JSONObject) jsonObject.getJSONArray("Result").get(0);
            String NUM = data.getString("NUM");
            String MINUTES = data.getString("MINUTES");
            String KM = data.getString("MINUTES");
            String CAL = data.getString("MINUTES");
            String TODAYNUM = data.getString("MINUTES");
            String PE = data.getString("PE");
            String TEACHER = data.getString("TEACHER");

            String[] result = new String[]{NUM, MINUTES, KM, CAL, TODAYNUM, PE, TEACHER};
            return result;
        } catch (JSONException e) {
            Toast.makeText(DetailActivity.this, "登陆失败！", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private String[] getCardData(String card){
        try {
            JSONObject jsonObject = new JSONObject(card);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray list = data.getJSONArray("list");
            JSONObject recentRecord = list.getJSONObject(0);
            String blance = recentRecord.getString("blance");
            String payment = recentRecord.getString("payment");
            String occurrenceTime = recentRecord.getString("occurrenceTime");
            String operationTitle = recentRecord.getString("operationTitle");
            String[] result = new String[]{blance, payment, occurrenceTime, operationTitle};
            return result;
        } catch (JSONException e) {
            Toast.makeText(DetailActivity.this, "登陆失败！", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }


    @Nullable
    private String[] getBookInfo(String book){
        try {
            JSONObject jsonObject = new JSONObject(book);
            JSONObject data = jsonObject.getJSONObject("data");
            String circsCount = data.getString("circsCount");
            String debt = data.getString("debt");
            String minDueDayBookDate = data.getString("minDueDayBookDate");
            String expiredBookCount = data.getString("expiredBookCount");
            String[] result = new String[]{circsCount, debt, minDueDayBookDate, expiredBookCount};
            return result;
        } catch (JSONException e) {
            Toast.makeText(DetailActivity.this, "登陆失败！", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }


    private void getTxtFileInfo(){
        try {
            File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/userinfo");
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String content = br.readLine();
            String[] contents = content.split("##");
            username = contents[0];
            password = contents[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        unbindService(JWConnection);
        super.onDestroy();
    }
}
