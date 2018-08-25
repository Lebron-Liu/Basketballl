package com.xykj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xykj.bean.Conversation;
import com.xykj.utils.Common;
import com.xykj.vwill.R;
import com.xyy.utils.XImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends ItemAdapter<Conversation> {
    public ConversationAdapter(Context context) {
        super(context);
    }

    @Override
    protected ViewHolder createHolder(int type) {
        View layout = LayoutInflater.from(context).inflate(R.layout.item_conversation, null);
        return new ConversationHolder(layout);
    }

    @Override
    protected void bindView(Conversation conversation, ViewHolder holder) {
        ConversationHolder h = (ConversationHolder) holder;
        h.itemLastMsg.setText(conversation.getLastMsg());
        h.itemTime.setText(conversation.getTime());
        if(conversation.getUnread()>0){
            h.itemUnread.setVisibility(View.VISIBLE);
            if(conversation.getUnread()>99){
                h.itemUnread.setText("99+");
            }else{
                h.itemUnread.setText(String.valueOf(conversation.getUnread()));
            }
        }else{
            h.itemUnread.setVisibility(View.GONE);
        }
        h.itemNick.setText(conversation.getChatter().getNick());
        if(conversation.isGroup()){
            //群组的默认头像
            h.itemIcon.setImageResource(R.drawable.ic_group);
        }else{
            String photo = conversation.getChatter().getPhoto();
            if(null != photo && !"".equals(photo)){
                XImageLoader.getInstance(context).showImage(Common.SERVER_URL+photo,h.itemIcon,R.drawable.icon_usr_def);
            }
        }
    }

    static class ConversationHolder extends ViewHolder {
        @BindView(R.id.item_icon)
        CircleImageView itemIcon;
        @BindView(R.id.item_nick)
        TextView itemNick;
        @BindView(R.id.item_last_msg)
        TextView itemLastMsg;
        @BindView(R.id.item_time)
        TextView itemTime;
        @BindView(R.id.item_unread)
        TextView itemUnread;

        ConversationHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
