package com.xykj.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mct.client.ClientManager;
import com.xykj.bean.User;
import com.xykj.utils.Common;
import com.xykj.vwill.LoginActivity;
import com.xykj.vwill.R;
import com.xykj.vwill.UserCenterActivity;
import com.xykj.vwill.VWillApp;
import com.xykj.vwill.VWillService;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;
import com.xyy.utils.TipsUtil;
import com.xyy.utils.XImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends Fragment {


    @BindView(R.id.iv_icon)
    CircleImageView ivIcon;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_sign)
    TextView tvSign;
    @BindView(R.id.tv_order)
    TextView tvOrder;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.tv_usr_msg)
    TextView tvUsrMsg;
    @BindView(R.id.tv_active_publish)
    TextView tvActivePublish;
    @BindView(R.id.tv_active_join)
    TextView tvActiveJoin;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.line2)
    View line2;
    Unbinder unbinder;
    private VWillApp app;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (VWillApp) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        showUserInfo();
    }

    private void showUserInfo() {
        if (app.isLogin()) {
            //显示用户基本信息
            User u = app.getLoginUser();
            String photo = u.getPhoto();
            if (photo != null && !"".equals(photo)&&!"null".equals(photo)) {
                XImageLoader.getInstance(getActivity()).showImage(Common.SERVER_URL + photo, ivIcon,R.drawable.icon_usr_def);
            }
            tvNick.setText(u.getNick());
            tvSex.setText(u.getSex());
            tvId.setText("ID:" + u.getId());
            tvSign.setText(u.getSign());
            tvActiveJoin.setVisibility(View.VISIBLE);
            tvOrder.setVisibility(View.VISIBLE);
            tvCollect.setVisibility(View.VISIBLE);
            tvUsrMsg.setVisibility(View.VISIBLE);
            tvActivePublish.setVisibility(View.VISIBLE);
            tvLogout.setVisibility(View.VISIBLE);
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
        } else {
            //游客信息
            ivIcon.setImageResource(R.drawable.icon_usr_def);
            tvNick.setText("游客");
            tvSex.setText("");
            tvId.setText("");
            tvSign.setText("");
            tvActiveJoin.setVisibility(View.INVISIBLE);
            tvOrder.setVisibility(View.INVISIBLE);
            tvCollect.setVisibility(View.INVISIBLE);
            tvUsrMsg.setVisibility(View.INVISIBLE);
            tvActivePublish.setVisibility(View.INVISIBLE);
            tvLogout.setVisibility(View.INVISIBLE);
            line1.setVisibility(View.INVISIBLE);
            line2.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick({R.id.iv_icon, R.id.tv_order, R.id.tv_collect, R.id.tv_usr_msg, R.id.tv_active_publish, R.id.tv_active_join, R.id.tv_settings, R.id.tv_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_icon:
                //检测是否有登录，没有则进入登录界面
                if (!app.isLogin()) {
                    Intent it = new Intent(getActivity(), LoginActivity.class);
                    startActivity(it);
                } else {
                    //已登录进入个人中心
                    Intent it = new Intent(getActivity(), UserCenterActivity.class);
                    it.putExtra("userId", app.getLoginUser().getId());
                    it.putExtra("nick", app.getLoginUser().getNick());
                    startActivity(it);
                }
                break;
            case R.id.tv_order:
                break;
            case R.id.tv_collect:
                break;
            case R.id.tv_usr_msg:
                break;
            case R.id.tv_active_publish:
                break;
            case R.id.tv_active_join:
                break;
            case R.id.tv_settings:
                break;
            case R.id.tv_logout:
                NetManager.getInstance().execute(new RequestItem.Builder()
                        .url(Common.URL_LOGOUT)
                        .addHead("token", Common.TOKEN)
                        .build(), new Callback<Boolean>() {
                    @Override
                    public Boolean changeData(ResponceItem responce) {
                        String json = responce.getString();
                        try {
                            JSONObject obj = new JSONObject(json);
                            return obj.optInt("result") == 1;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    public void onResult(Boolean result) {
                        if (result != null && result) {
                            //登出ApacheMina服务器不接受即时消息，停止VWillService
                            ClientManager.getInstance().logout(app.getLoginUser().getId());
                            Intent it = new Intent(getActivity(), VWillService.class);
                            getActivity().stopService(it);
                            app.setLoginUser(null);
                            Common.TOKEN = null;
                            //发送注销成功的广播
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(Common.ACTION_LOGOUT));
                            showUserInfo();
                            TipsUtil.toast(getActivity(), "注销成功");
                        }
                    }
                });
                break;
        }
    }
}
