package com.npu.zhang.npuassistant;

import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.npu.zhang.npuassistant.Model.AXDataService;
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

public class CardViewActivity extends AppCompatActivity implements View.OnClickListener{

    private String username;
    private String password;
    public static MyCookie myCookie;
    private ProgressDialog dialog;
    private boolean AXFlag = true;
    private boolean JWFlag = true;
    private boolean AXServiceFlag = false;
    private boolean JWServiceFlag = false;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.npu.zhang.npuassistant.finishJWLogin":
                    if (JWServiceFlag){
                        unbindService(JWConnection);
                    }
                    JWFlag = false;
                    dialog.dismiss();
                    dialog.setMessage("正在获取数据");
                    dialog.show();
                    System.out.println("finishJWLogin");
                    bindService(new Intent(CardViewActivity.this, JWDataService.class), JWConnection, BIND_AUTO_CREATE);
                    break;
                case "com.npu.zhang.npuassistant.finishAXLogin":
                    AXFlag = false;
                    System.out.println("finishAXLogin");
                    bindService(new Intent(CardViewActivity.this, AXDataService.class), AXConnection, BIND_AUTO_CREATE);
                    break;
                case "com.npu.zhang.npuassistant.finishJWDataGet":
                    JWFlag = true;
                    JWServiceFlag = false;
                    System.out.println("finishJWDataGet");
                    unbindService(JWConnection);

                    ((TextView)findViewById(R.id.tv_TestName)).setText(intent.getStringExtra("testName"));
                    ((TextView)findViewById(R.id.tv_TestLocation)).setText(intent.getStringExtra("testLocation"));
                    ((TextView)findViewById(R.id.tv_TestDate)).setText(intent.getStringExtra("testDate"));
                    ((TextView)findViewById(R.id.tv_TestTime)).setText(intent.getStringExtra("testTime"));

                    break;
                case "com.npu.zhang.npuassistant.finishAXDataGet":
                    AXFlag = true;
                    AXServiceFlag = false;
                    System.out.println("finishAXDataGet");
                    unbindService(AXConnection);

                    ((TextView)findViewById(R.id.tv_cardblance)).setText(intent.getStringExtra("cardblance"));
                    ((TextView)findViewById(R.id.tv_payment)).setText(intent.getStringExtra("payment"));
                    ((TextView)findViewById(R.id.tv_occurrenceTime)).setText(intent.getStringExtra("occurrenceTime"));
                    ((TextView)findViewById(R.id.tv_operationTitle)).setText(intent.getStringExtra("operationTitle"));

                    ((TextView)findViewById(R.id.tv_circsCount)).setText(intent.getStringExtra("circsCount"));
                    ((TextView)findViewById(R.id.tv_debt)).setText(intent.getStringExtra("debt"));
                    ((TextView)findViewById(R.id.tv_minDueDayBookDate)).setText(intent.getStringExtra("minDueDayBookDate"));
                    ((TextView)findViewById(R.id.tv_expiredBookCount)).setText(intent.getStringExtra("expiredBookCount"));
                    break;
            }
            if (JWFlag && AXFlag){
                dialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);
        findView();
//        getData();
        IntentFilter intentFilter = new IntentFilter("com.npu.zhang.npuassistant.finishJWLogin");
        intentFilter.addAction("com.npu.zhang.npuassistant.finishAXLogin");
        intentFilter.addAction("com.npu.zhang.npuassistant.finishJWDataGet");
        intentFilter.addAction("com.npu.zhang.npuassistant.finishAXDataGet");
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
            JWServiceFlag = true;
            bindService(intent, JWConnection, BIND_AUTO_CREATE);
        }
        else{
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private final ServiceConnection JWConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private final ServiceConnection AXConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void findView(){
        findViewById(R.id.cv_card).setOnClickListener(this);
        findViewById(R.id.cv_papertest).setOnClickListener(this);
        findViewById(R.id.cv_grading).setOnClickListener(this);
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
        if (JWServiceFlag){
            unbindService(JWConnection);
        }
        if (AXServiceFlag){
            unbindService(AXConnection);
        }
        super.onDestroy();
    }

    private void startDetailActivity(String url, String cookie){
        Intent intent = new Intent(CardViewActivity.this, WebDetailActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("cookie", cookie);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_card:
                if (myCookie.getAXCookie() == null){
                    Toast.makeText(CardViewActivity.this, "请先登录翱翔系统", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CardViewActivity.this, LoginActivity.class));
                }
                else {
                    startDetailActivity(CARD_WEB_URL, myCookie.getAXCookie());
                }
                break;
            case R.id.cv_grading:
                startDetailActivity(GRADING_WEB_URL, myCookie.getJWCookie());
                break;
            case R.id.cv_papertest:
                startDetailActivity(PAPERTEST_WEB_URL, myCookie.getJWCookie());
                break;
        }
    }
}
