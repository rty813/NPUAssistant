package com.npu.zhang.npuassistant;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by zhang on 2017/6/12.
 */

public class MyProgressDialog extends Dialog {

    private ProgressBar pb_ax;
    private ProgressBar pb_jw;
    private ImageView iv_ax_right;
    private ImageView iv_jw_right;
    private ImageView iv_ax_wrong;
    private ImageView iv_jw_wrong;

    public MyProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydialog);
        findView();
    }

    public void show_pb_ax(int type){
        pb_ax.setVisibility(type);
    }
    public void show_pb_jw(int type){
        pb_jw.setVisibility(type);
    }
    public void show_iv_ax_right(int type){
        iv_ax_right.setVisibility(type);
    }
    public void show_iv_jw_right(int type){
        iv_jw_right.setVisibility(type);
    }
    public void show_iv_ax_wrong(int type){
        iv_ax_wrong.setVisibility(type);
    }
    public void show_iv_jw_wrong(int type){
        iv_jw_wrong.setVisibility(type);
    }

    private void findView(){
        pb_ax = (ProgressBar) findViewById(R.id.pb_ax);
        pb_jw = (ProgressBar) findViewById(R.id.pb_jw);
        iv_ax_right = (ImageView) findViewById(R.id.iv_ax_right);
        iv_jw_right = (ImageView) findViewById(R.id.iv_jw_right);
        iv_ax_wrong = (ImageView) findViewById(R.id.iv_ax_wrong);
        iv_jw_wrong = (ImageView) findViewById(R.id.iv_jw_wrong);
    }
}
