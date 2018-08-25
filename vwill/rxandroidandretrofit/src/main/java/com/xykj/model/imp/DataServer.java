package com.xykj.model.imp;

import com.xykj.model.Article;
import com.xykj.model.ErrorResult;
import com.xykj.model.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * 数据获取接口
 * Retrofit单独使用时的数据接口
 */
public interface DataServer {

    //@Get里边的参数表示功能的相对url
    //@Query表示向地址后加参数（Get方式请求有效），如http://127.0.0.1:8080/AskMeServer/list?type=参数值
    //设置1个参数
    @GET("list")
    Call<List<Article>> getArticles(@Query("type") Integer type);
    //@Query多固定数量参数表示，如下
//    @GET("list")
//    Call<List<Article>> getArticles(@Query("type") Integer type,@Query("page")Integer page);

    //@QueryMap表示向地址后加多个参数（Get方式请求有效），如http://127.0.0.1:8080/AskMeServer/list?type=1&page=2
    //其中Map中的key就是参数名，value表示参数值，encoded = true表示参数已经使用UrlEcoder
    @GET("list")
    Call<List<Article>> getArticles(@QueryMap(encoded = true) Map<String, String> options);

//    @Path表示URL地址中有参数，如：http://127.0.0.1:8080/AskMeServer/delete/1
//    @GET("delete/{newsId}")
//    Call<NewsBean> deleteItem(@Path("newsId") String newsId);

    //@Field表示Post方式请求的表单参数，跟@Query类似，但是@Query只能用在地址上，参数在内容中需要@Field
    //@FieldMap类似@QueryMap，只是@FieldMap用在Post方式请求中
    @FormUrlEncoded    //使用@Field时记得添加@FormUrlEncoded
    @POST("login")
    Call<User> login(@Field("name") String name, @Field("psw") String psw);

    //提交表单内容以及单文件上传
    @Multipart
    @POST("regist")
    Call<ErrorResult> register(@PartMap Map<String, RequestBody> parMap, @Part MultipartBody.Part file);

    //提交表单内容以及单多文件上传
    @Multipart
    @POST("publish")
    Call<ErrorResult> publishArticle(@PartMap Map<String, RequestBody> parMap, @PartMap Map<String, MultipartBody.Part> files);


}
