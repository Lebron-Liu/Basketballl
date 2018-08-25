package com.xykj.persenter;

import com.xykj.bean.Article;
import com.xykj.model.ArticleModel;
import com.xykj.utils.Common;
import com.xykj.view.ArticleView;
import com.xyy.net.RequestItem;

import java.util.List;

public class ArticlePersenter extends IPersenter<ArticleView> {

    /**
     * 根据类型(标签)加载文章
     * @param type
     * @param tagId
     * @param page
     */
    public void loadArticleByType(int type, int tagId, int page) {
        StringBuilder sb = new StringBuilder();
        if (type != 0) {
            sb.append("type=").append(type).append("&");
        }
        if (tagId != 0) {
            sb.append("tag_id=").append(tagId).append("&");
        }
        if (page != 0) {
            sb.append("page=").append(page);
        }
        int len = sb.length();
        if (sb.charAt(len - 1) == '&') {
            sb.replace(len - 1, len, "");
        }
        String url = Common.URL_LOAD_ARTICLE + "?" + sb.toString();
        RequestItem request = new RequestItem.Builder()
                .url(url).build();
        new ArticleModel(request).execute(new BaseCallback<List<Article>>(this, view) {
            @Override
            public void onSuccess(List<Article> data) {
                view.showArticles(data);
            }
        });
    }

    /**
     * 加载用户关注的文章
     * @param userId
     */
    public void loadAttentionArticles(int userId) {
        new ArticleModel(new RequestItem.Builder()
                .url(Common.URL_LOAD_ATTENTION + "?u_id=" + userId)
                .build()).execute(new BaseCallback<List<Article>>(this, view) {
            @Override
            public void onSuccess(List<Article> data) {
                view.showArticles(data);
            }
        });
    }

    public void loadArticleByUser(int userId){
        new ArticleModel(new RequestItem.Builder()
                .url(Common.URL_ARTICLE_BY_USER + "?u_id=" + userId)
                .build()).execute(new BaseCallback<List<Article>>(this, view) {
            @Override
            public void onSuccess(List<Article> data) {
                view.showArticles(data);
            }
        });
    }
}
