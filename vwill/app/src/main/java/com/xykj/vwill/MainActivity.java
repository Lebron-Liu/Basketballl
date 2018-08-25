package com.xykj.vwill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xykj.bean.User;
import com.xykj.fragments.FoundFragment;
import com.xykj.fragments.HomeFragment;
import com.xykj.fragments.MsgFragment;
import com.xykj.utils.Common;
import com.xyy.utils.XImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.item_icon)
    CircleImageView itemIcon;
    @BindView(R.id.title_center_tx)
    TextView titleCenterTx;
    @BindView(R.id.title_center_view)
    FrameLayout titleCenterView;
    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.group_tabs)
    RadioGroup groupTabs;
    @BindView(R.id.drawerlayout)
    DrawerLayout drawerlayout;
    private RadioGroup titleTabs;
    private LogoutReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        groupTabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.tab_home:
                        //显示下标为0的内容页
                        showFragment(0);
                        break;
                    case R.id.tab_found:
                        //显示下标为1的内容页
                        showFragment(1);
                        break;
                    case R.id.tab_msg:
                        //显示下标为2的内容页
                        showFragment(2);
                        break;
                }
            }
        });
        //初始化第一个页面的标题布局
        titleTabs = (RadioGroup) getLayoutInflater().inflate(R.layout.title_tab_layout, null);
        titleCenterView.addView(titleTabs);
        //选中标题上卡片时处理内容页显示
        titleTabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.title_tab_left:
                        //显示精选的内容(文章、图像、视频、音乐)
                        ((HomeFragment) fragments[0]).showFragment(0);
                        break;
                    case R.id.title_tab_right:
                        //显示关注
                        ((HomeFragment) fragments[0]).showFragment(1);
                        break;
                }
            }
        });
        //处理默认显示的页面（社区）
        showFragment(0);
        receiver = new LogoutReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.ACTION_LOGOUT);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    //记录当前显示的内容页下标
    private int currentIndex = -1;
    private Fragment[] fragments = new Fragment[3]; // {null,null,null}

    /**
     * 根据下标显示内容页
     *
     * @param index
     */
    private void showFragment(int index) {
        if (currentIndex != index) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //如果已经有旧内容在显示 隐藏旧的dettach
            if (currentIndex != -1) {
                ft.detach(fragments[currentIndex]);
            }
            //显示新的：
            if (fragments[index] == null) {
                //第一次显示：创建对象（记录） 添加
                switch (index) {
                    case 0:
                        fragments[0] = new HomeFragment();
                        break;
                    case 1:
                        fragments[1] = new FoundFragment();
                        break;
                    case 2:
                        fragments[2] = new MsgFragment();
                        break;
                }
                ft.add(R.id.content, fragments[index]);
            } else {
                //已有对象 显示attach
                ft.attach(fragments[index]);
            }
            ft.commit();
            currentIndex = index;
            //处理标题情况
            if (index == 0) {
                //显示标题选项卡
                titleCenterView.setVisibility(View.VISIBLE);
                titleCenterTx.setVisibility(View.GONE);
            } else {
                //显示标题文本
                if (titleCenterView.getVisibility() == View.VISIBLE) {
                    titleCenterTx.setVisibility(View.VISIBLE);
                    titleCenterView.setVisibility(View.GONE);
                }
            }
        }
    }

    @OnClick({R.id.item_icon, R.id.iv_publish, R.id.iv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.item_icon:
                //检测侧滑侧滑菜单有没有显示，没有显示则显示出来
                if (!drawerlayout.isDrawerOpen(Gravity.START)) {
                    drawerlayout.openDrawer(Gravity.START);
                }
                break;
            case R.id.iv_publish:
                Intent it = new Intent(this,ArticlePublishActivity.class);
                startActivity(it);
                break;
            case R.id.iv_search:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (fragments[i] != null) {
                if (i == 0) {
                    ((HomeFragment) fragments[0]).removeAllFragment();
                }
                ft.remove(fragments[i]);
                fragments[i] = null;
            }
        }
        ft.commitAllowingStateLoss();
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        receiver = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        VWillApp app = (VWillApp) getApplication();
        if (app.isLogin()) {
            //显示用户基本信息
            User u = app.getLoginUser();
            String photo = u.getPhoto();
            if (photo != null && !"".equals(photo) && !"null".equals(photo)) {
                XImageLoader.getInstance(this).showImage(Common.SERVER_URL + photo, itemIcon, R.drawable.icon_usr_def);
            }
        }

    }

    class LogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            itemIcon.setImageResource(R.drawable.icon_usr_def);
        }
    }
}
