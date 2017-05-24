package com.npu.zhang.npuassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView tv_book;
    private TextView tv_card;
    private TextView tv_schedule;
    private TextView tv_literature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tv_book = (TextView) findViewById(R.id.tv_book);
        tv_card = (TextView) findViewById(R.id.tv_card);
        tv_schedule = (TextView) findViewById(R.id.tv_schedule);
        tv_literature = (TextView) findViewById(R.id.tv_literatrue);


        Intent intent = getIntent();
        tv_book.setText(intent.getStringExtra("book"));
        tv_card.setText(intent.getStringExtra("card"));
        tv_schedule.setText(intent.getStringExtra("schedule"));
        tv_literature.setText(intent.getStringExtra("literature"));
    }
}
