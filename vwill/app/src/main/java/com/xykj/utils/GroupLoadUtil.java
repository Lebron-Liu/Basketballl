package com.xykj.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSON;
import com.xykj.bean.GroupInfo;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;
import com.xyy.utils.TipsUtil;
import com.xyy.view.FloatingLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * 群组选择的工具
 */
public class GroupLoadUtil {
    //记录已经加载过的群组列表
    private List<GroupInfo> list;
    //记录用户的选择
    private List<GroupInfo> userSelectList = new LinkedList<>();

    private PopupWindow window;
    private FloatingLayout floatingLayout;

    public void showSelectWindow(final Context context, View view, final int num) {
        if (null == window) {
            ScrollView scrollView = new ScrollView(context);
            scrollView.setBackgroundColor(0xffffffff);
            floatingLayout = new FloatingLayout(context);
            floatingLayout.setOnItemCheckedChangeListener(new FloatingLayout.OnItemCheckedChangeListener() {
                @Override
                public void onItemCheckedChange(int position, boolean isChecked) {
                    GroupInfo g = list.get(position);
                    if (isChecked) {
                        userSelectList.add(g);
                    } else {
                        userSelectList.remove(g);
                    }
                }

                @Override
                public boolean isCanCheck(int position, boolean currentChecked) {
                    if (!currentChecked && userSelectList.size() == num) {
                        TipsUtil.toast(context, "选择数量已达上限");
                        return false;
                    }
                    return true;
                }
            });
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            scrollView.setLayoutParams(lp);
            //标签布局
            ScrollView.LayoutParams lp1 = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT);
            scrollView.addView(floatingLayout, lp1);
            window = new PopupWindow(scrollView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setFocusable(true);
            window.setOutsideTouchable(true);
            window.setBackgroundDrawable(new ColorDrawable());
            window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (null != onSelectListener) {
                        if(!userSelectList.isEmpty()) {
                            onSelectListener.onSelectResult(userSelectList);
                        }
                    }
                }
            });
        }
        if (list != null && list.size() > 0) {
            Message m = new Message();
            m.what = 1;
            m.obj = view;
            handler.sendMessageDelayed(m, 800);
        }
        loadGroupInfo(view);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (NetManager.getInstance().isTaskRunning(Common.URL_LOAG_ALL_GROUP)) {
                NetManager.getInstance().cancel(Common.URL_LOAG_ALL_GROUP);
                window.showAsDropDown((View) msg.obj);
            }
        }
    };

    private void loadGroupInfo(final View view) {
        NetManager.getInstance().execute(new RequestItem.Builder()
                .url(Common.URL_LOAG_ALL_GROUP)
                .build(), new Callback<List<GroupInfo>>() {
            @Override
            public List<GroupInfo> changeData(ResponceItem responce) {
                String jsonStr = responce.getString();
                if (jsonStr.startsWith("[")) {
                    List<GroupInfo> list = JSON.parseArray(jsonStr, GroupInfo.class);
                    return list;
                }
                return null;
            }

            @Override
            public void onResult(List<GroupInfo> result) {
                if (null != result) {
                    list = result;
                    //如果当前有用户的选择则将已选的内容标出(默认是选中)
                    if (!userSelectList.isEmpty()) {
                        //移除之前的显示
                        floatingLayout.removeAllViews();
                        //重新添加新的标签
                        List<GroupInfo> tempList = new LinkedList<>();
                        tempList.addAll(userSelectList);
                        //加载到的内容数量
                        int size = result.size();
                        for (int i = 0; i < size; i++) {
                            GroupInfo g = result.get(i);
                            int index = 0;
                            boolean isFound = false; //记录是否找到重复的
                            while (index < tempList.size()) {
                                if (g.equals(tempList.get(index))) {
                                    isFound = true;
                                    tempList.remove(index);
                                    break;
                                }
                                index++;
                            }
                            floatingLayout.addItem(g, "getGroupName", isFound);
                        }
                    } else {
                        floatingLayout.setTags(result, "getGroupName");
                    }
                    window.showAsDropDown(view);
                }
            }
        });
    }

    public interface OnSelectListener {
        void onSelectResult(List<GroupInfo> list);
    }

    private OnSelectListener onSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }
}
