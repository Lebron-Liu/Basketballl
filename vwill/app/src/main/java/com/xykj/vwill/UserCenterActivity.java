package com.xykj.vwill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.xykj.adapter.UserCenterPagerAdapter;
import com.xykj.bean.User;
import com.xykj.persenter.UserInfoPersenter;
import com.xykj.utils.Common;
import com.xykj.view.BaseActivity;
import com.xykj.view.UserInfoView;
import com.xyy.utils.TipsUtil;
import com.xyy.utils.XImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserCenterActivity extends BaseActivity<UserInfoPersenter> implements UserInfoView {
    private static final int REQUEST_LOGIN = 1;
    @BindView(R.id.iv_user_icon)
    CircleImageView ivUserIcon;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_chat_or_edit_info)
    TextView tvChatOrEditInfo;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.tv_sign)
    TextView tvSign;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.m_pager)
    ViewPager mPager;
    private VWillApp app;
    private int userId;
    private String nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent it = getIntent();
        userId = it.getIntExtra("userId", 0);
        nick = it.getStringExtra("nick");
        //查看个人信息，如果是本人（登陆者，直接可以从app中获取），如果不是登陆者那么可以用用户id加载用户信息来查看
        super.onCreate(savedInstanceState);

        tvId.setText("ID:" + userId);
        tvNick.setText(nick);
        //是否是登陆者
        app = (VWillApp) getApplication();
        if (app.isLogin() && app.getLoginUser().getId() == userId) {
            //是登陆者，直接显示信息
            showUserInfo(app.getLoginUser());
            tvChatOrEditInfo.setText("修改信息");
            tvTitleRight.setText("修改信息");
            tvMsg.setVisibility(View.GONE);
        } else {
            tvChatOrEditInfo.setText("悄悄话");
            tvTitleRight.setText("悄悄话");
            //加载用户信息来显示
            persenter.loadUserInfo(userId);
        }
        UserCenterPagerAdapter pagerAdapter = new UserCenterPagerAdapter(getSupportFragmentManager(), userId);
        mPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(mPager);
        //AppBarLayout的监听
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //检测是否全部滑到屏幕外
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (tvTitleRight.getVisibility() == View.GONE) {
                        tvTitleRight.setVisibility(View.VISIBLE);
                        tvTitleCenter.setText(nick);
                    }
                } else {
                    if (tvTitleRight.getVisibility() != View.GONE) {
                        tvTitleRight.setVisibility(View.GONE);
                        tvTitleCenter.setText("用户中心");
                    }
                }
            }
        });
    }

    @Override
    protected int getType() {
        return 0;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_user_center;
    }

    @OnClick({R.id.tv_chat_or_edit_info, R.id.tv_msg, R.id.tv_title_left, R.id.tv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_msg:
                break;
            case R.id.tv_title_left:
                finish();
                break;
            case R.id.tv_chat_or_edit_info:
            case R.id.tv_title_right:
                if (app.isLogin()) {
                    toNextActivity();
                } else {
                    //去登陆
                    Intent it = new Intent(this, LoginActivity.class);
                    startActivityForResult(it, REQUEST_LOGIN);
                }
                break;
        }
    }

    private void toNextActivity() {
        if (app.getLoginUser().getId() == userId) {
            //修改个人信息
            TipsUtil.toast(this, "修改个人信息");
        } else {
            //去聊天(发送即时消息)
            Intent it = new Intent(UserCenterActivity.this, ChatActivity.class);
            it.putExtra("userId", userId);
            it.putExtra("nick", nick);
            if (null != user) {
                it.putExtra("photo", user.getPhoto());
            }
            startActivity(it);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                toNextActivity();
            }
        }

    }

    private User user;

    @Override
    public void showUserInfo(User user) {
        this.user = user;
        tvSex.setText(user.getSex());
        tvSign.setText(user.getSign());
        //头像
        String photo = user.getPhoto();
        if (photo != null && !"".equals(photo)) {
            XImageLoader.getInstance(this).showImage(Common.SERVER_URL + photo, ivUserIcon, R.drawable.icon_usr_def);
        }
    }
}
