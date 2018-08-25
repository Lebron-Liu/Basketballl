package com.xykj.view;

import com.xykj.bean.Article;

import java.util.List;

public interface ArticleView extends IView{

    void showArticles(List<Article> list);
}
