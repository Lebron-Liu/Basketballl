package com.xykj.holder;

import com.xykj.bean.Article;

public interface OnArticleListener {
    void onSupportOrNoSupport(Article article, int state, ArticleHolder holder);

    void onArticleClick(Article article);
}
