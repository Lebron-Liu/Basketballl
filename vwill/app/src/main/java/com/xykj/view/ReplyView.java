package com.xykj.view;

import java.util.List;

public interface ReplyView extends IView {
    /**
     * 显示热评
     * @param list
     */
    void showHotReplies(List<Object> list);

    /**
     * 显示最新评论
     * @param list
     */
    void showNewReplies(List<Object> list);
}
