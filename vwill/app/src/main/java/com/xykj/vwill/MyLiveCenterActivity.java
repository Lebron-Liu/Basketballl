package com.xykj.vwill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.xykj.bean.LiveRoom;
import com.xykj.fragments.GetPictureFragment;
import com.xykj.persenter.LiveRoomPersenter;
import com.xykj.utils.Common;
import com.xykj.view.BaseActivity;
import com.xykj.view.LiveCenterView;
import com.xyy.net.FileRequestItem;
import com.xyy.net.NetManager;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;
import com.xyy.utils.TipsUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MyLiveCenterActivity extends BaseActivity<LiveRoomPersenter> implements LiveCenterView {

    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.btn_create)
    Button btnCreate;
    @BindView(R.id.create_room_layout)
    LinearLayout createRoomLayout;
    private LiveRoom liveRoom;
    //封面图片地址
    private String photoPath;
    private VWillApp app;

    private SharedPreferences mSharedPreferences = null;

    private boolean isOritationSwitcherChecked = false;

    // Bitrate related params
    private int mSupportedBitrateValues[] = new int[]{2000, 1200, 800, 600};

    // Resolution related params
    private int mSupportedResolutionValues[] = new int[]{1920, 1080, 1280, 720, 640, 480, 480, 360};
    private int mSelectedResolutionIndex = 1;

    // Frame rate ralated params
    private int mSupportedFramerateValues[] = new int[]{18, 15, 15, 15};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = (VWillApp) getApplication();
        super.onCreate(savedInstanceState);
        //检测用户是否有房间
        persenter.loadLiveRoom(app.getLoginUser().getId());
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        mSharedPreferences = getApplication().getSharedPreferences("BCELive", Context.MODE_PRIVATE);
        initUIElements();
    }
    private void initUIElements() {
        fetchStreamParams();
        RadioGroup orientationRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        final RadioButton radioLandscape = (RadioButton) findViewById(R.id.radioLandscape);
        final RadioButton radioPortrait = (RadioButton) findViewById(R.id.radioPortrait);
        orientationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioLandscape) {
                    isOritationSwitcherChecked = true;
                    radioLandscape.setTextColor(Color.WHITE);
                    radioPortrait.setTextColor(0xff666666);
                } else {
                    isOritationSwitcherChecked = false;
                    radioLandscape.setTextColor(0xff666666);
                    radioPortrait.setTextColor(Color.WHITE);
                }
            }
        });
        if (isOritationSwitcherChecked) {
            radioLandscape.setChecked(true);
            radioLandscape.setTextColor(Color.WHITE);
        } else {
            radioPortrait.setChecked(true);
            radioPortrait.setTextColor(Color.WHITE);
        }

        RadioGroup resolutionRadioGroup = (RadioGroup) findViewById(R.id.radioGroup0);
        final RadioButton radio1080P = (RadioButton) findViewById(R.id.radio1080p);
        final RadioButton radio720P = (RadioButton) findViewById(R.id.radio720p);
        final RadioButton radio480P = (RadioButton) findViewById(R.id.radio480p);
        final RadioButton radio360P = (RadioButton) findViewById(R.id.radio360p);
        resolutionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radio1080P.setTextColor(0xff666666);
                radio720P.setTextColor(0xff666666);
                radio480P.setTextColor(0xff666666);
                radio360P.setTextColor(0xff666666);
                switch (checkedId) {
                    case R.id.radio1080p:
                        mSelectedResolutionIndex = 0;
                        radio1080P.setTextColor(Color.WHITE);
                        break;
                    case R.id.radio720p:
                        mSelectedResolutionIndex = 1;
                        radio720P.setTextColor(Color.WHITE);
                        break;
                    case R.id.radio480p:
                        mSelectedResolutionIndex = 2;
                        radio480P.setTextColor(Color.WHITE);
                        break;
                    case R.id.radio360p:
                        mSelectedResolutionIndex = 3;
                        radio360P.setTextColor(Color.WHITE);
                        break;
                }
            }
        });

        switch (mSelectedResolutionIndex) {
            case 0:
                radio1080P.setChecked(true);
                radio1080P.setTextColor(Color.WHITE);
                break;
            case 1:
                radio720P.setChecked(true);
                radio720P.setTextColor(Color.WHITE);
                break;
            case 2:
                radio480P.setChecked(true);
                radio480P.setTextColor(Color.WHITE);
                break;
            case 3:
                radio360P.setChecked(true);
                radio360P.setTextColor(Color.WHITE);
                break;
        }

    }

    private void fetchStreamParams() {
        mSelectedResolutionIndex = mSharedPreferences.getInt("resolution", 1);
        isOritationSwitcherChecked = mSharedPreferences.getBoolean("oritation_landscape", false);
    }

    private void saveStreamParams() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("resolution", mSelectedResolutionIndex);
        editor.putBoolean("oritation_landscape", isOritationSwitcherChecked);
        editor.commit();
    }
    @Override
    protected int getType() {
        return TYPE_BACK;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_my_live_center;
    }

    @OnClick({R.id.tv_start_streaming, R.id.photo, R.id.btn_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_start_streaming:
                onClickPush();
                break;
            case R.id.photo:
                showGetPicDialog();
                break;
            case R.id.btn_create:
                FileRequestItem.Builder b = new FileRequestItem.Builder()
                        .url(Common.URL_CREATE_ROOM)
                        .addStringParam("u_id", String.valueOf(app.getLoginUser().getId()));
                if (null != photoPath) {
                    b.addFileParam("photo", new File(photoPath));
                }
                NetManager.getInstance().execute(b.build(), new Callback<LiveRoom>() {
                    @Override
                    public LiveRoom changeData(ResponceItem responce) {
                        String json = responce.getString();
                        if (!json.startsWith("{result")) {
                            liveRoom = (LiveRoom) JSON.parseObject(json, LiveRoom.class);
                            return liveRoom;
                        }
                        return null;
                    }

                    @Override
                    public void onResult(LiveRoom result) {
                        if (result != null) {
                            createRoomLayout.setVisibility(View.GONE);
                            TipsUtil.toast(MyLiveCenterActivity.this, "创建成功");
                        }
                    }
                });
                break;
        }
    }

    public void onClickPush() {
        Intent intent = new Intent(MyLiveCenterActivity.this, StreamingActivity.class);
        //主播需要推流地址
        intent.putExtra("push_url", liveRoom.getPushUrl());
        intent.putExtra("res_w", mSupportedResolutionValues[mSelectedResolutionIndex * 2]);
        intent.putExtra("res_h", mSupportedResolutionValues[mSelectedResolutionIndex * 2 + 1]);
        intent.putExtra("frame_rate", mSupportedFramerateValues[mSelectedResolutionIndex]);
        intent.putExtra("bitrate", mSupportedBitrateValues[mSelectedResolutionIndex]);
        intent.putExtra("oritation_landscape", isOritationSwitcherChecked);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        saveStreamParams();
        super.onPause();
    }

    private GetPictureFragment getPictureFragment;

    private void showGetPicDialog() {
        if (null == getPictureFragment) {
            getPictureFragment = new GetPictureFragment();
            getPictureFragment.setIs1v1Size(false);
            getPictureFragment.setOnGetPictureListener(new GetPictureFragment.OnGetPictureListener() {
                @Override
                public void onResult(Uri uri) {
                    photoPath = uri.getPath();
                    photo.setImageURI(uri);
                }
            });
        }
        getPictureFragment.show(getSupportFragmentManager(), "pic");
    }

    @Override
    public void showLiveRooms(List<LiveRoom> rooms) {
        if (rooms.size() > 0) {
            liveRoom = rooms.get(0);
            createRoomLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected String getActivityTitle() {
        return"我的直播间";
    }
}
