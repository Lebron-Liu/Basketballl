package com.xykj.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.xykj.bean.Article;
import com.xykj.holder.ArticleHolder;
import com.xykj.holder.OnArticleListener;
import com.xykj.utils.Common;
import com.xykj.vwill.ArticleDetailActivity;
import com.xykj.vwill.LoginActivity;
import com.xykj.vwill.VWillApp;
import com.xyy.net.NetManager;
import com.xyy.net.ResponceItem;
import com.xyy.net.StringRequestItem;
import com.xyy.net.imp.Callback;
import com.xyy.utils.TipsUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ArticleListener implements OnArticleListener {
    public static final int REQUEST_LOGIN = 10001;
    //记录当前要操作的文章对象
    private Article optArticle;
    //记录要操作的(赞/踩)状态
    private int optState;
    private ArticleHolder optHolder;
    Activity activity;
    private VWillApp app;
    private Object obj;

    public ArticleListener(Object obj) {
        this.obj = obj;
        if (obj instanceof Fragment) {
            activity = ((Fragment) obj).getActivity();
        }else{
            activity = ((Activity)obj);
        }
        app = (VWillApp) activity.getApplication();
    }

    @Override
    public void onSupportOrNoSupport(Article article, int state, ArticleHolder holder) {
        optArticle = article;
        optState = state;
        optHolder = holder;
        //检测用户是否登录，如果没有登录则取去登录
        if (app.isLogin()) {
            //如果已经登录直接处理赞/踩或者取消
            supportOrNoSupport();
        } else {
            Intent it = new Intent(activity, LoginActivity.class);
            if (obj instanceof Fragment) {
                ((Fragment) obj).startActivityForResult(it, REQUEST_LOGIN);
            }else{
                ((Activity)obj).startActivityForResult(it, REQUEST_LOGIN);
            }
        }
    }

    @Override
    public void onArticleClick(Article article) {
        Intent it = new Intent(activity, ArticleDetailActivity.class);
        it.putExtra("article",article);
        activity.startActivity(it);

    }

    private void supportOrNoSupport() {
        if (optArticle.getSupportState() == optState) {
            //取消
            NetManager.getInstance().execute(new StringRequestItem.Builder()
                    .url(Common.URL_SUPPORT_CANCEL)
                    .addStringParam("articleId", String.valueOf(optArticle.getId()))
                    .addHead("token", Common.TOKEN)
                    .build(), new Callback<Boolean>() {
                @Override
                public Boolean changeData(ResponceItem responce) {
                    String json = responce.getString();
                    try {
                        JSONObject obj = new JSONObject(json);
                        return obj.optInt("result") == 1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        optArticle.setSupportState(0);
                        if (optState == 1) {
                            optArticle.setSupport(optArticle.getSupport() - 1);
                            TipsUtil.toast(activity, "赞-1");
                            optHolder.setSupportState(optState, optArticle.getSupport(), false);
                        } else {
                            optArticle.setNoSupport(optArticle.getNoSupport() - 1);
                            TipsUtil.toast(activity, "踩-1");
                            optHolder.setSupportState(optState, optArticle.getNoSupport(), false);
                        }
                    }
                }
            });
        } else {
            NetManager.getInstance().execute(
                    new StringRequestItem.Builder()
                            .url(Common.URL_SUPPORT)
                            .addStringParam("articleId", String.valueOf(optArticle.getId()))
                            .addStringParam("state", String.valueOf(optState))
                            .addHead("token", Common.TOKEN)
                            .build(), new Callback<Boolean>() {
                        @Override
                        public Boolean changeData(ResponceItem responce) {
                            String json = responce.getString();
                            try {
                                JSONObject obj = new JSONObject(json);
                                return obj.optInt("result") == 1;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }

                        @Override
                        public void onResult(Boolean result) {
                            if (result) {
                                // 成功
                                //将赞或者踩的状态设置到文章中
                                optArticle.setSupportState(optState);
                                //赞+1  或者 踩+1
                                if (optState == 1) {
                                    optArticle.setSupport(optArticle.getSupport() + 1);
                                    TipsUtil.toast(activity, "赞+1");
                                    optHolder.setSupportState(optState, optArticle.getSupport(), true);
                                } else {
                                    optArticle.setNoSupport(optArticle.getNoSupport() + 1);
                                    TipsUtil.toast(activity, "踩+1");
                                    optHolder.setSupportState(optState, optArticle.getNoSupport(), true);
                                }
                            } else {
                                //失败
                            }
                        }
                    }
            );
        }
    }

    public void loginResult(int resultCode){
        if (resultCode == Activity.RESULT_OK) {
            //直接处理赞/踩或者取消
            supportOrNoSupport();
        } else {
            //恢复原样
            if(optState==1){
                optHolder.setSupportState(1,optArticle.getSupport(),false);
            }else{
                optHolder.setSupportState(-1,optArticle.getNoSupport(),false);
            }
            optArticle = null;
            optState = 0;
            optHolder = null;
        }
    }

}
