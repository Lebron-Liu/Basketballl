package com.xykj.rxandroidandretrofit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xykj.model.Article;
import com.xykj.model.ErrorResult;
import com.xykj.model.ErrorResultException;
import com.xykj.model.User;
import com.xykj.model.imp.DataServer;
import com.xykj.utils.Common;
import com.xykj.utils.HttpManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetrofitActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RetrofitActivity";
    private DataServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        findViewById(R.id.btn_get).setOnClickListener(this);
        findViewById(R.id.btn_post).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        server = HttpManager.getInstance(getApplicationContext()).getDataServer();
    }

    private void getTest() {
        Map<String, String> par = new HashMap<>();
        par.put("type", "1");
        par.put("page", "1");
//        Call<List<Article>> call = server.getArticles(1);
        Call<List<Article>> call = server.getArticles(par);
        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful()) {
                    List<Article> list = response.body();
                    Log.e(TAG, "==>size:" + list.size() + " ==>" + Thread.currentThread().getName());
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "fail==>" + Thread.currentThread().getName());
            }
        });
    }

    private void postTest() {
        Call<User> call = server.login("qqq", "111");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "user===>" + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof ErrorResultException) {
                    ErrorResultException e = (ErrorResultException) t;
                    Log.e(TAG, "==>" + e.toString());
                }
            }
        });
    }

    private void uploadTest() {
        File file = new File("/mnt/sdcard/pic9.jpg");
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part和后端约定好Key，这里的partName是用image
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        //表单数据（普通的文本）
        Map<String,RequestBody> map = new HashMap<>();
        map.put("nick",RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"retrofit02"));
        map.put("psw",RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"123456"));
        map.put("sex",RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"男"));
        map.put("sign",RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"abchello"));
        Call<ErrorResult> call = server.register(map,body);
        call.enqueue(new Callback<ErrorResult>() {
            @Override
            public void onResponse(Call<ErrorResult> call, Response<ErrorResult> response) {
                Log.e(TAG,"onResponse====>"+response.body().toString());
            }

            @Override
            public void onFailure(Call<ErrorResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                getTest();
                break;
            case R.id.btn_post:
                postTest();
                break;
            case R.id.btn_upload:
                uploadTest();
                break;
        }
    }
}
