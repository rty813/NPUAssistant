package com.npu.zhang.npuassistant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_codeimage;
    private EditText et_username;
    private EditText et_password;

    private ImageView imageView;
    private Button btn_login;
    private OkHttpClient client;
    private String lt;
    private Handler mHandler;
    private String username = "2015300955";
    private String password = "J2mv9jyyq6";
    private String imagecode;
    private String text;

    private static final String CODEIMAGEURL = "https://uis.nwpu.edu.cn/cas/codeimagetest";
    private static final String LOGIN_URL = "https://uis.nwpu.edu.cn/cas/login?service=https%3A%2F%2Fecampus.nwpu.edu.cn%2Fc%2Fportal%2Flogin";
    private static final String CARD_URL = "https://ecampus.nwpu.edu.cn/web/guest/index?p_p_id=indexuserdata_WAR_jigsawportalindexuserdataportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=loadCardData&p_p_cacheability=cacheLevelPage&p_p_col_id=column-1&p_p_col_pos=1&p_p_col_count=3";
    private static final String LITERATURE_URL = "https://ecampus.nwpu.edu.cn/web/guest/index?p_p_id=indexuserdata_WAR_jigsawportalindexuserdataportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=loadLiteratureData&p_p_cacheability=cacheLevelPage&p_p_col_id=column-1&p_p_col_pos=1&p_p_col_count=3";
    private static final String BOOK_URL = "https://ecampus.nwpu.edu.cn/web/guest/index?p_p_id=indexuserdata_WAR_jigsawportalindexuserdataportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=loadBookData&p_p_cacheability=cacheLevelPage&p_p_col_id=column-1&p_p_col_pos=1&p_p_col_count=3";
    private static final String SCHEDULE_URL = "https://ecampus.nwpu.edu.cn/web/guest/calendar?p_p_id=calendar_WAR_jigsawportalcalendarportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=loadEventsForBootstrapCalendar&p_p_cacheability=cacheLevelPage&p_p_col_id=column-1&p_p_col_count=1&_calendar_WAR_jigsawportalcalendarportlet_from=1493568000000&_calendar_WAR_jigsawportalcalendarportlet_to=1496246400000&_calendar_WAR_jigsawportalcalendarportlet_utc_offset=-480&_calendar_WAR_jigsawportalcalendarportlet_browser_timezone=Asia%2FShanghai";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_codeimage = (EditText) findViewById(R.id.et_imagecode);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        imageView = (ImageView) findViewById(R.id.imageview);
        btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(this);
        getTxtFileInfo();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/codeImage.jpg");
                        Bitmap bitmap1 = BitmapFactory.decodeFile(String.valueOf(file));
                        imageView.setImageBitmap(bitmap1);
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };



        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client = new OkHttpClient().newBuilder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
//                获取lt
                Request.Builder builder = new Request.Builder().url(LOGIN_URL);
                Request request = builder.build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    String html = response.body().string();
                    Document document = Jsoup.parse(html);
                    Elements inputTags = document.select("input");
                    for (Element inputTag : inputTags) {
                        if (inputTag.attr("name").equals("lt")) {
                            lt = inputTag.attr("value");
                            System.out.println(lt);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                获取验证码
                builder = new Request.Builder().url(CODEIMAGEURL);
                request = builder.build();
                call = client.newCall(request);
                try {
                    Response response = call.execute();
                    InputStream inputStream = response.body().byteStream();
                    byte[] bytes = new byte[1024];
                    File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/codeImage.jpg");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    int size;
                    while ((size = inputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, size);
                    }
                    inputStream.close();
                    fileOutputStream.close();
                    mHandler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                username = et_username.getText().toString();
                password = et_password.getText().toString();
                imagecode = et_codeimage.getText().toString();
                saveUserInfo(username, password);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RequestBody body = new FormBody.Builder()
                                .add("username", username)
                                .add("password", password)
                                .add("lt", lt)
                                .add("_eventId", "submit")
                                .add("imageCodeName", imagecode)
                                .add("errors", "0")
                                .build();
                        Request request = new Request.Builder()
                                .url(LOGIN_URL)
                                .post(body)
                                .build();
                        Call call = client.newCall(request);
                        try {
                            call.execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        //card
                        request = new Request.Builder()
                                .url(CARD_URL)
                                .build();
                        call = client.newCall(request);
                        try {
                            Response response = call.execute();
                            intent.putExtra("card", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //book
                        request = new Request.Builder()
                                .url(BOOK_URL)
                                .build();
                        call = client.newCall(request);
                        try {
                            Response response = call.execute();
                            intent.putExtra("book", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //literature
                        request = new Request.Builder()
                                .url(LITERATURE_URL)
                                .build();
                        call = client.newCall(request);
                        try {
                            Response response = call.execute();
                            intent.putExtra("literature", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //schedule
                        request = new Request.Builder()
                                .url(SCHEDULE_URL)
                                .build();
                        call = client.newCall(request);
                        try {
                            Response response = call.execute();
                            intent.putExtra("schedule", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                }).start();
                break;
        }
    }

    private boolean saveUserInfo(String username, String password){
        try{
            File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/userinfo");
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write((username + "##" + password).getBytes());
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
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
            et_username.setText(username);
            et_password.setText(password);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}