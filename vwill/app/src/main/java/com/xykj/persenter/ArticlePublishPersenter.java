package com.xykj.persenter;

import com.xykj.bean.Article;
import com.xykj.bean.MediaItem;
import com.xykj.model.ArticleModel;
import com.xykj.model.ArticlePublishModel;
import com.xykj.utils.Common;
import com.xykj.view.BooleanView;
import com.xyy.net.FileRequestItem;

import java.io.File;
import java.util.List;

public class ArticlePublishPersenter extends IPersenter<BooleanView> {

    /**
     * 发布文章
     *
     * @param title
     * @param content
     * @param type
     * @param isHide
     * @param tagId
     * @param lat
     * @param lng
     * @param locName
     */
    public void publishArticle(String title, String content, int type, boolean isHide, int tagId, double lat, double lng, String locName, List<MediaItem> medias) {
        FileRequestItem.Builder b = new FileRequestItem.Builder()
                .url(Common.URL_PUBLISH_ARTICLE)
                .addStringParam("title", title)
                .addStringParam("content", content)
                .addStringParam("isHide", isHide ? "1" : "0")
                .addStringParam("type", String.valueOf(type));
        //标签
        if (tagId != 0) {
            b.addStringParam("tag_id", String.valueOf(tagId));
        }
        //位置
        if (lat != -1) {
            b.addStringParam("lat", String.valueOf(lat));
            b.addStringParam("lng", String.valueOf(lng));
            b.addStringParam("loc_name", locName);
        }
        //媒体文件
        if (null != medias && !medias.isEmpty()) {
            int size = medias.size();
            for (int i = 0; i < size; i++) {
                b.addFileParam("media_" +i, new File(medias.get(i).getPath()));
            }
        }
        //token信息
        b.addHead("token", Common.TOKEN);
        new ArticlePublishModel(b.build()).execute(new BaseCallback<Boolean>(this, view) {
            @Override
            public void onSuccess(Boolean data) {
                view.showResult(data);
            }
        });
    }
}
