package com.npu.zhang.npuassistant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.npu.zhang.npuassistant.UrlCollection.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_codeimage;
    private EditText et_username;
    private EditText et_password;

    private ImageView imageView;
    private Button btn_login;
    private OkHttpClient client;
    private String lt;
    private byte[] pic_bt;
    private Handler mHandler;
    private String username = "2015300955";
    private String password = "J2mv9jyyq6";
    private String imagecode;
    private ProgressDialog progressDialog;

    private CookieManager cookieManager;
    public static MyCookie myCookie;

    public class ReceivedCookiesInterceptor implements Interceptor{
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            List<String> cookieList =  originalResponse.headers("Set-Cookie");
            if(cookieList != null) {
                for(String s:cookieList) {//Cookie的格式为:cookieName=cookieValue;path=xxx
                    System.out.println("Cookie!!!!:::::" + s);
                }
            }
            return originalResponse;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCookie = new MyCookie();
        et_codeimage = (EditText) findViewById(R.id.et_imagecode);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        imageView = (ImageView) findViewById(R.id.imageview);
        btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(this);
        imageView.setOnClickListener(this);
        getTxtFileInfo();

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("登陆中");
        progressDialog.setCancelable(false);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Bitmap bitmap = BitmapFactory.decodeByteArray(pic_bt, 0, pic_bt.length);
                        imageView.setImageBitmap(bitmap);
                        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(imageView.getLayoutParams());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(marginLayoutParams);
                        layoutParams.height = et_codeimage.getHeight();
                        imageView.setLayoutParams(layoutParams);
                        break;
                    case 1:
                        progressDialog.dismiss();
                        break;
                }
            }
        };

        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client = new OkHttpClient().newBuilder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        new GetImageCode().start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                progressDialog.show();
                username = et_username.getText().toString();
                password = et_password.getText().toString();
                imagecode = et_codeimage.getText().toString();
                saveUserInfo(username, password);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        模拟登陆翱翔门户
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
                        List<HttpCookie> cookieList = cookieManager.getCookieStore().getCookies();

                        myCookie.setAXCookie(cookieList.get(1).toString());


                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("card", getData(CARDDETAIL_URL));
                        intent.putExtra("book", getData(BOOK_URL));
                        intent.putExtra("schedule", getData(SCHEDULE_URL));
                        intent.putExtra("exercise", getData(EXERCISE_URL + username + "%7D"));
//                        模拟登陆教务系统
                        body = new FormBody.Builder()
                                .add("username", username)
                                .add("password", password)
                                .build();
                        request = new Request.Builder()
                                .url(JW_LOGIN_URL)
                                .post(body)
                                .build();
                        call = client.newCall(request);
                        try {
                            call.execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        cookieList = cookieManager.getCookieStore().getCookies();
                        for (int i = cookieList.size()-1; i>=0; i--){
                            if ((cookieList.get(i).toString()).contains("JSESSIONID")){
                                myCookie.setJWCookie(cookieList.get(i).toString());
                                break;
                            }
                        }
                        intent.putExtra("exam", getData(JW_EXAM_URL));
                        mHandler.sendEmptyMessage(1);
                        startActivity(intent);
                    }
                }).start();
                break;
            case R.id.imageview:
                new GetImageCode().start();
        }
    }

    private String getData(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
//            List<HttpCookie> cookieList = cookieManager.getCookieStore().getCookies();
//            for (HttpCookie cookie : cookieList){
//                System.out.println(cookie);
//            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class GetImageCode extends Thread{
        @Override
        public void run() {
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
                pic_bt = response.body().bytes();
                mHandler.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
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