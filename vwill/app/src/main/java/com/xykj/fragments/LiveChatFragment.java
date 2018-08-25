package com.xykj.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.mct.client.ClientManager;
import com.mct.model.CMessage;
import com.xykj.adapter.LiveMsgAdapter;
import com.xykj.bean.VWillMessage;
import com.xykj.vwill.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveChatFragment extends Fragment {

    @BindView(R.id.list_live_msg)
    ListView listLiveMsg;
    @BindView(R.id.et_content)
    EditText etContent;
    Unbinder unbinder;
    //聊天的房间号
    private int roomId;
    private int loginUserId;
    private LiveMsgAdapter adapter;

    public LiveChatFragment() {
        // Required empty public constructor
    }

    public void initInfo(int loginUserId,int roomId) {
        this.loginUserId = loginUserId;
        this.roomId = roomId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new LiveMsgAdapter(getActivity());
        listLiveMsg.setAdapter(adapter);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VWillMessage msg) {
        adapter.addMsg(msg);
        //让ListView滚动到新增的数据上
        listLiveMsg.smoothScrollToPosition(adapter.getCount() - 1);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_send)
    public void onViewClicked() {
        String msg = etContent.getText().toString();
        if(!TextUtils.isEmpty(msg)){
            ClientManager.getInstance().sendTextMsg(loginUserId,roomId,msg);
            //显示
            VWillMessage m = new VWillMessage(loginUserId,"","",msg,VWillMessage.TYPE_SEND, CMessage.TYPE_TEXT);
            adapter.addMsg(m);
            listLiveMsg.smoothScrollToPosition(adapter.getCount()-1);
            etContent.setText("");
        }
    }

}
