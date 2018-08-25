package com.xykj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xykj.bean.FunctionItem;
import com.xykj.vwill.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FunctionAdapter extends ItemAdapter<FunctionItem> {

    public FunctionAdapter(Context context) {
        super(context);
        list = new ArrayList<>(6);
        list.add(new FunctionItem("附近人", "Ta在附近\n近在咫尺", R.drawable.icon_nearby));
        list.add(new FunctionItem("活动", "妙趣横生\n热闹非凡", R.drawable.icon_active));
        list.add(new FunctionItem("美食", "美味邂逅\n美食享受", R.drawable.icon_food));
        list.add(new FunctionItem("直播间", "德才兼备\n技压群雄", R.drawable.icon_live_room));
        list.add(new FunctionItem("文章", "缘游至此\n情舒此刻", R.drawable.icon_near_article));
        list.add(new FunctionItem("接朋友", "你来的陌生之地\n有我等候", R.drawable.icon_get_friend));
    }

    @Override
    protected ViewHolder createHolder(int itemType) {
        View layout = LayoutInflater.from(context).inflate(R.layout.item_function, null);
        return new FunctionHolder(layout);
    }

    @Override
    protected void bindView(FunctionItem functionItem, ViewHolder holder) {
        FunctionHolder h = (FunctionHolder) holder;
        h.itemIcon.setImageResource(functionItem.getIcon());
        h.itemTitle.setText(functionItem.getTitle());
        h.itemDesc.setText(functionItem.getDescript());
    }

    class FunctionHolder extends ViewHolder{
        @BindView(R.id.item_icon)
        ImageView itemIcon;
        @BindView(R.id.item_title)
        TextView itemTitle;
        @BindView(R.id.item_desc)
        TextView itemDesc;
        public FunctionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
