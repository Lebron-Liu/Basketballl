package com.xykj.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xykj.bean.VWillMessage;
import com.xykj.vwill.R;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveMsgAdapter extends ItemAdapter<VWillMessage> {

    public LiveMsgAdapter(Context context) {
        super(context);
    }

    @Override
    protected ViewHolder createHolder(int itemType) {
        View layout = LayoutInflater.from(context).inflate(R.layout.item_live_msg_layout, null);
        return new MsgHolder(layout);
    }

    @Override
    protected void bindView(VWillMessage vWillMessage, ViewHolder holder) {
        MsgHolder h = (MsgHolder) holder;
        if(vWillMessage.getType() == VWillMessage.TYPE_SEND){
            h.itemNick.setText("我:");
            h.itemNick.setTextColor(Color.RED);
            h.itemMsg.setTextColor(0xff00ff00);
        }else{
            h.itemNick.setText(vWillMessage.getNick()+":");
            h.itemNick.setTextColor(0xffff4081);
            h.itemMsg.setTextColor(0xffffffff);
        }
        h.itemMsg.setText(vWillMessage.getMsg());
    }

    class MsgHolder extends ViewHolder {
        @BindView(R.id.item_nick)
        TextView itemNick;
        @BindView(R.id.item_msg)
        TextView itemMsg;
        public MsgHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void addMsg(VWillMessage m){
        if(list == null){
            list = new LinkedList<>();
        }
        list.add(m);
        notifyDataSetChanged();
    }
}
