package com.linhh.ptrrv.android_pulltorefreshrecyclerview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/**
 * Created by linhonghong on 2015/11/13.
 */
public class MainActivity extends AppCompatActivity {

    private Button mBtnGridViewMode,mBtnListViewMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

    }

    private void findViews(){
        mBtnGridViewMode = (Button) this.findViewById(R.id.btn_gv_mode);
        mBtnGridViewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.
                        startActivity(
                                new Intent(MainActivity.this, PtrrvGridViewMode.class));
            }
        });
        mBtnListViewMode = (Button) this.findViewById(R.id.btn_lv_mode);
        mBtnListViewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.
                        startActivity(
                                new Intent(MainActivity.this,PtrrvListViewMode.class));
            }
        });
    }

}
