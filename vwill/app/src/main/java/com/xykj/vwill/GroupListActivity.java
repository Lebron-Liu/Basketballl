package com.xykj.vwill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xykj.adapter.GroupListAdapter;
import com.xykj.bean.GroupInfo;
import com.xykj.persenter.GroupListPersenter;
import com.xykj.view.BaseActivity;
import com.xykj.view.GroupListView;

import java.util.List;

public class GroupListActivity extends BaseActivity<GroupListPersenter> implements GroupListView {

    private GroupListAdapter adapter;
    @Override
    protected int getType() {
        return TYPE_BACK_MENU;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_group_list;
    }

    @Override
    public void showGroupList(List<Object> data) {
        adapter.setList(data);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        VWillApp app = (VWillApp) getApplication();
        super.onCreate(savedInstanceState);
        persenter.loadGroupList(app.getLoginUser().getId());
    }

    @Override
    protected void initLayout() {
        ListView listView = findViewById(R.id.list_group);
        adapter = new GroupListAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                GroupInfo g = (GroupInfo) adapter.getItem(position);
                Intent it = new Intent(GroupListActivity.this,ChatActivity.class);
                it.putExtra("userId",g.getId());
                it.putExtra("nick",g.getGroupName());
                it.putExtra("photo","");
                it.putExtra("is_group",1);
                startActivity(it);
            }
        });
    }

    @Override
    protected String getActivityTitle() {
        return "群聊";
    }
}
