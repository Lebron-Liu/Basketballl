package com.xykj.vwill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.xykj.adapter.MediaAdapterInPubish;
import com.xykj.bean.GroupInfo;
import com.xykj.bean.MediaItem;
import com.xykj.persenter.ArticlePublishPersenter;
import com.xykj.utils.Common;
import com.xykj.utils.GroupLoadUtil;
import com.xykj.view.BaseActivity;
import com.xykj.view.BooleanView;
import com.xykj.widget.XGridView;
import com.xyy.utils.TipsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ArticlePublishActivity extends BaseActivity<ArticlePublishPersenter> implements BooleanView {
    //获取位置
    private static final int REQUEST_LOCATION = 1;
    //获取媒体
    private static final int REQUEST_MEDIA = 2;
    private static final int REQUEST_LOGIN = 3;
    @BindView(R.id.radio_image)
    RadioButton radioImage;
    @BindView(R.id.radio_video)
    RadioButton radioVideo;
    @BindView(R.id.radio_audio)
    RadioButton radioAudio;
    @BindView(R.id.et_title)
    TextInputEditText etTitle;
    @BindView(R.id.tv_tag_select)
    TextView tvTagSelect;
    @BindView(R.id.et_content)
    TextInputEditText etContent;
    @BindView(R.id.grid_media)
    XGridView gridMedia;
    @BindView(R.id.tv_loc)
    TextView tvLoc;
    @BindView(R.id.ch_hide)
    CheckBox chHide;
    @BindView(R.id.iv_cancel_loc)
    ImageView ivCancelLoc;
    @BindView(R.id.group_type)
    RadioGroup groupType;
    //标签选择器
    private GroupLoadUtil groupLoadUtil;
    //用户选择的标签的id（群号）
    private int userSelectTagId;
    //记录用户选择的位置和名称
    private LatLng selectLocation;
    private String selectLocatioName;
    //要发布的文章类型
    private int type = Common.TYPE_TEXT;
    //发布文章的媒体列表
    private ArrayList<MediaItem> medias;
    private MediaAdapterInPubish mediaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getType() {
        return TYPE_CANCEL_SURE;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_article_publish;
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        groupLoadUtil = new GroupLoadUtil();
        groupLoadUtil.setOnSelectListener(new GroupLoadUtil.OnSelectListener() {
            @Override
            public void onSelectResult(List<GroupInfo> list) {
                GroupInfo info = list.get(0);
                tvTagSelect.setText("#" + info.getGroupName() + "#");
                userSelectTagId = info.getId();
            }
        });
        mediaAdapter = new MediaAdapterInPubish(this, R.drawable.ic_add);
        gridMedia.setAdapter(mediaAdapter);
        gridMedia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mediaAdapter.getItem(i) == null) {
                    //点击了“+”
                    toGetMedia(type);
                }
            }
        });
    }

    @Override
    public void showResult(boolean result) {
        if (result) {
            TipsUtil.toast(this, "发布成功");
            finish();
        }
    }

    @OnClick({R.id.radio_image, R.id.radio_video, R.id.radio_audio, R.id.tv_tag_select, R.id.tv_loc, R.id.iv_cancel_loc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.radio_image:
                toGetMedia(Common.TYPE_IMAGE);
                break;
            case R.id.radio_video:
                toGetMedia(Common.TYPE_VIDEO);
                break;
            case R.id.radio_audio:
                toGetMedia(Common.TYPE_AUDIO);
                break;
            case R.id.tv_tag_select:
                groupLoadUtil.showSelectWindow(this, view, 1);
                break;
            case R.id.tv_loc:
                Intent it = new Intent(this, MapActivity.class);
                startActivityForResult(it, REQUEST_LOCATION);
                break;
            case R.id.iv_cancel_loc:
                selectLocation = null;
                selectLocatioName = null;
                tvLoc.setText("选择位置");
                ivCancelLoc.setVisibility(View.GONE);
                break;
        }
    }

    private void toGetMedia(int type) {
        Intent it = new Intent(this, GetMediaActivity.class);
        it.putExtra("type", type);
        switch (type) {
            case Common.TYPE_IMAGE:
                it.putExtra("max", 9);
                mediaAdapter.setMaxSize(9);
                break;
            case Common.TYPE_VIDEO:
                it.putExtra("max", 1);
                mediaAdapter.setMaxSize(1);
                break;
            default:
                it.putExtra("max", 2);
                mediaAdapter.setMaxSize(2);
                break;
        }
        mediaAdapter.setType(type);
        //如果是同类型（这次取的和上次取的类型一样），有可能之前有选择过，因此可以将已经选择的内容传到获取媒体界面标出来
        if (type == this.type && medias != null) {
            it.putExtra("data", medias);
        }
        startActivityForResult(it, REQUEST_MEDIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    selectLocatioName = data.getStringExtra("address");
                    selectLocation = data.getParcelableExtra("location");
                    tvLoc.setText(selectLocatioName);
                    ivCancelLoc.setVisibility(View.VISIBLE);
                }
                break;
            case REQUEST_MEDIA:
                if (resultCode == RESULT_OK) {
                    //有数据
                    int returnType = data.getIntExtra("type", 0);
                    if (type != returnType) {
                        type = returnType;
                    }
                    medias = data.getParcelableArrayListExtra("result");
                    if (gridMedia.getVisibility() != View.VISIBLE) {
                        gridMedia.setVisibility(View.VISIBLE);
                    }
                    //显示
                    mediaAdapter.setList(medias);
                } else {
                    //没有数据,恢复原来的类型
                    switch (type) {
                        case Common.TYPE_TEXT:
                            groupType.clearCheck();
                            gridMedia.setVisibility(View.GONE);
                            break;
                        case Common.TYPE_IMAGE:
                            radioImage.setChecked(true);
                            break;
                        case Common.TYPE_VIDEO:
                            radioVideo.setChecked(true);
                            break;
                        case Common.TYPE_AUDIO:
                            radioAudio.setChecked(true);
                            break;
                    }
                }
                break;
            case REQUEST_LOGIN:
                if(resultCode == RESULT_OK){
                    publish();
                }
                break;
        }
    }

    @Override
    protected String getActivityTitle() {
        return "文章发布";
    }

    @Override
    protected void onTitleRightViewClick(View v) {
        VWillApp app = (VWillApp) getApplication();
        if (app.isLogin()) {
            publish();
        } else {
            Intent it = new Intent(this, LoginActivity.class);
            startActivityForResult(it, REQUEST_LOGIN);
        }
    }

    private void publish() {
        String title = etTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            TipsUtil.toast(this, "标题不能为空");
            return;
        }
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            TipsUtil.toast(this, "内容不能为空");
            return;
        }
        double lat = -1, lng = -1;
        if (null != selectLocation) {
            lat = selectLocation.latitude;
            lng = selectLocation.longitude;
        }
        persenter.publishArticle(title, content, type, chHide.isChecked(), userSelectTagId, lat, lng, selectLocatioName, medias);
    }
}
