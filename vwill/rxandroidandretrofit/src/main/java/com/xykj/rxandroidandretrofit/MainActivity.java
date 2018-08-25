package com.xykj.rxandroidandretrofit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.m_btn_01).setOnClickListener(this);
        findViewById(R.id.m_btn_02).setOnClickListener(this);
        findViewById(R.id.m_btn_03).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.m_btn_01:
                //RxAndroid的使用
                Intent it1 = new Intent(MainActivity.this, RxAndroidActivity.class);
                startActivity(it1);
                break;
            case R.id.m_btn_02:
                //Retrofit的使用
                Intent it2 = new Intent(MainActivity.this, RetrofitActivity.class);
                startActivity(it2);
                break;
            case R.id.m_btn_03:
                //综合运用
                Intent it3 = new Intent(MainActivity.this,RxAndroidAndRetrofit.class);
                startActivity(it3);
                break;
        }
    }

}
