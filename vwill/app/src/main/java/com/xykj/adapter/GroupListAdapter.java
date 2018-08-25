package com.xykj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.xykj.bean.GroupInfo;
import com.xykj.vwill.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupListAdapter extends ItemAdapter<Object> {
    //标签
    private static final int TYPE_TAG = 1;
    //群组信息
    private static final int TYPE_GROUP = 2;

    public GroupListAdapter(Context context) {
        super(context);
    }

    @Override
    protected ViewHolder createHolder(int type) {
        if (type == TYPE_TAG) {
            TextView tv = new TextView(context);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lp);
            tv.setTextSize(20);
            tv.setPadding(5, 5, 5, 5);
            tv.setBackgroundColor(0xFF999999);
            tv.setTextColor(0xFF333333);
            return new TipsHolder(tv);
        } else {
            View layout = LayoutInflater.from(context).inflate(R.layout.item_group_info, null);
            return new GroupHolder(layout);
        }
    }

    @Override
    protected void bindView(Object o, ViewHolder holder) {
        if (o instanceof String) {
            TextView tv = (TextView) holder.itemView;
            tv.setText(String.valueOf(o));
        } else {
            GroupInfo g = (GroupInfo) o;
            GroupHolder h = (GroupHolder) holder;
            String name = g.getGroupName() + "(" + g.getMembers() + ")";
            h.itemName.setText(name);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof String) {
            return TYPE_TAG;
        }
        return TYPE_GROUP;
    }


    class TipsHolder extends ViewHolder {

        public TipsHolder(View itemView) {
            super(itemView);
        }
    }

    class GroupHolder extends ViewHolder {
        @BindView(R.id.item_name)
        TextView itemName;

        public GroupHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_detail)
        public void onViewClicked() {
            //进入详情

        }
    }

    @Override
    public boolean isEnabled(int position) {
        if (list.get(position) instanceof String) {
            return false;
        }
        return true;
    }
}
