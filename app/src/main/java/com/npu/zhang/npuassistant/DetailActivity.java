package com.npu.zhang.npuassistant;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);
        findView();
        getData();
    }

    private void findView(){
    }

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

}
