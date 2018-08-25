package com.xykj.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xykj.bean.LiveRoom;
import com.xykj.utils.Common;
import com.xykj.vwill.R;
import com.xyy.utils.XImageLoader;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class LiveRoomAdapter extends RecyclerView.Adapter<LiveRoomAdapter.LiveRoomHolder> {
    private List<LiveRoom> list;
    private Context context;
    //每个item宽度（屏幕的宽度一半）
    private int itemWidth;

    public LiveRoomAdapter(Context context) {
        this.context = context;
        itemWidth = context.getResources().getDisplayMetrics().widthPixels / 2;
    }

    @NonNull
    @Override
    public LiveRoomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layout = LayoutInflater.from(context).inflate(R.layout.item_live_room, null);
        return new LiveRoomHolder(layout, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveRoomHolder holder, int i) {
        LiveRoom room = list.get(i);
        holder.itemUserNick.setText(room.getAnchorName());
        //封面
        String photo = room.getPhoto();
        if (null != photo && !"".equals(photo)) {
            XImageLoader.getInstance(context).showImage(Common.SERVER_URL + photo, holder.itemPhoto, itemWidth, 0, R.drawable.live_room_pic_def, false);
        } else {
            holder.itemPhoto.setImageResource(R.drawable.live_room_pic_def);
        }
        //主播头像
        String userPhoto = room.getAnchorPhoto();
        if (null != userPhoto && !"".equals(userPhoto)) {
            XImageLoader.getInstance(context).showImage(Common.SERVER_URL + userPhoto, holder.itemUserIcon, R.drawable.icon_usr_def);
        } else {
            holder.itemUserIcon.setImageResource(R.drawable.icon_usr_def);
        }
        //状态
        String stateStr = "准备中";
        if (room.getState() == 1) {
            stateStr = "直播中";
        }
        holder.itemState.setText(stateStr);
        holder.itemNum.setText("人数(" + room.getMembers() + ")");
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setList(List<LiveRoom> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class LiveRoomHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_photo)
        ImageView itemPhoto;
        @BindView(R.id.item_user_icon)
        CircleImageView itemUserIcon;
        @BindView(R.id.item_user_nick)
        TextView itemUserNick;
        @BindView(R.id.item_state)
        TextView itemState;
        @BindView(R.id.item_num)
        TextView itemNum;
        private WeakReference<LiveRoomAdapter> adapter;

        public LiveRoomHolder(@NonNull View itemView, LiveRoomAdapter liveRoomAdapter) {
            super(itemView);
            adapter = new WeakReference<>(liveRoomAdapter);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.item_user_icon, R.id.item_user_nick, R.id.item_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.item_user_icon:
                case R.id.item_user_nick:
                    //进入主播的个人中心

                    break;
                case R.id.item_view:
                    //进入直播观看界面
                    LiveRoomAdapter a = adapter.get();
                    if (null != a && a.onLiveRoomClickListener != null) {
                        LiveRoom room = a.list.get(getLayoutPosition());
                        a.onLiveRoomClickListener.onLiveRoomClick(room);
                    }
                    break;
            }
        }
    }

    private OnLiveRoomClickListener onLiveRoomClickListener;

    public void setOnLiveRoomClickListener(OnLiveRoomClickListener onLiveRoomClickListener) {
        this.onLiveRoomClickListener = onLiveRoomClickListener;
    }

    public interface OnLiveRoomClickListener {
        void onLiveRoomClick(LiveRoom room);
    }
}
