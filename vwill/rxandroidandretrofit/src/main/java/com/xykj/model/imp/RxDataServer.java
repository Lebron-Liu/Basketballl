package com.xykj.model.imp;

import com.xykj.model.Article;
import com.xykj.model.User;

import io.reactivex.Observable;
import retrofit2.http.*;

import java.util.List;

/**
 * Retrofit结合RxJava使用时的数据接口
 */
public interface RxDataServer {

    @GET("list")
    Observable<List<Article>> getArticles(@Query("type") Integer type);

    @FormUrlEncoded    //使用@Field时记得添加@FormUrlEncoded
    @POST("login")
    Observable<User> login(@Field("name") String name, @Field("psw") String psw);

}
