package com.xykj.fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xykj.adapter.ConversationAdapter;
import com.xykj.bean.Conversation;
import com.xykj.utils.Common;
import com.xykj.utils.DbUtil;
import com.xykj.vwill.ChatActivity;
import com.xykj.vwill.GroupListActivity;
import com.xykj.vwill.LoginActivity;
import com.xykj.vwill.R;
import com.xykj.vwill.VWillApp;

import java.util.List;

/**
 * 消息页面
 */
public class MsgFragment extends Fragment implements View.OnClickListener {
    private static final int REQUST_LOGIN_2_GROUPLIST = 1; //登录取群组列表
    private ConversationAdapter adapter;
    private VWillApp app;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_msg, container, false);
    }

    private LogoutReceiver receiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (VWillApp) getActivity().getApplication();
        //注册用户注销登录的广播，用户注销时清除会话列表
        receiver = new LogoutReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.ACTION_LOGOUT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ListView listView = view.findViewById(R.id.list_conversation);
        //设置适配器
        if (null == adapter) {
            adapter = new ConversationAdapter(getActivity());
        }
        listView.setAdapter(adapter);
        //添加“群组”，“黑名单”两个入口
        LinearLayout headView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.list_msg_head, null);
        listView.addHeaderView(headView);
        headView.findViewById(R.id.tv_to_group_list).setOnClickListener(this);
        headView.findViewById(R.id.tv_to_black_list).setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //介于ListView添加了headView所以内容部分的获取数据需要按照position-1的位置来获取
                Conversation con = adapter.getItem(position - 1);
                //更新会话为已读
                if (con.getUnread() > 0) {
                    DbUtil.getInstance(getActivity()).update2Readed(con.getId());
                }
                Intent it = new Intent(getActivity(), ChatActivity.class);
                //将会话id传过去
                it.putExtra("threads_id", con.getId());
                it.putExtra("is_group", con.isGroup() ? 1 : 0);
                //将聊天者信息传过去
                it.putExtra("chatter", con.getChatter());
                startActivity(it);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_to_group_list:
                //检测用户是否登录，登录了直接进入即可
                if (app.isLogin()) {
                    Intent it = new Intent(getActivity(), GroupListActivity.class);
                    startActivity(it);
                } else {
                    //未登录需要先去登录
                    Intent it = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(it,REQUST_LOGIN_2_GROUPLIST );
                }
                break;
            case R.id.tv_to_black_list:

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUST_LOGIN_2_GROUPLIST:
                if(resultCode == Activity.RESULT_OK){
                    Intent it = new Intent(getActivity(), GroupListActivity.class);
                    startActivity(it);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //加载数据
        VWillApp app = (VWillApp) getActivity().getApplication();
        if (app.isLogin()) {
            new LoadConversationTask().execute(app.getLoginUser().getId());
        }
    }

    class LoadConversationTask extends AsyncTask<Integer, Void, List<Conversation>> {

        @Override
        protected List<Conversation> doInBackground(Integer... objects) {
            int id = objects[0];
            return DbUtil.getInstance(getActivity()).loadConversations(id);
        }

        @Override
        protected void onPostExecute(List<Conversation> conversations) {
            if (conversations != null) {
                adapter.setList(conversations);
            }
        }
    }

    class LogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //用户注销，清空会话列表
            adapter.setList(null);
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onDestroy();
    }
}
