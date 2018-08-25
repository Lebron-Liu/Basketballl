package com.xykj.vwill;

import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xykj.fragments.GetPictureFragment;
import com.xykj.bean.GroupInfo;
import com.xykj.persenter.RegistPersenter;
import com.xykj.utils.GroupLoadUtil;
import com.xykj.view.BaseActivity;
import com.xykj.view.BooleanView;
import com.xyy.utils.Md5Util;
import com.xyy.utils.TipsUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegistActivity extends BaseActivity<RegistPersenter> implements BooleanView {

    @BindView(R.id.iv_icon)
    CircleImageView ivIcon;
    @BindView(R.id.et_name)
    TextInputEditText etName;
    @BindView(R.id.et_psw)
    TextInputEditText etPsw;
    @BindView(R.id.et_re_psw)
    TextInputEditText etRePsw;
    @BindView(R.id.group_sex)
    RadioGroup groupSex;
    @BindView(R.id.et_sign)
    TextInputEditText etSign;
    @BindView(R.id.tag_layout)
    LinearLayout tagLayout;
    //要上传的头像
    private String photoPath;
    //用户所要加入的群组
    private List<GroupInfo> userSelectGroups;

    @Override
    protected int getType() {
        return TYPE_CANCEL_SURE;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_regist;
    }

    @Override
    public void showResult(boolean result) {
        if (result) {
            TipsUtil.toast(this, "注册成功");
            finish();
        }
    }

    @Override
    protected void onTitleRightViewClick(View v) {
        //提交注册信息
        String nick = etName.getText().toString();
        if (!TextUtils.isEmpty(nick)) {
            String psw = etPsw.getText().toString();
            if (!TextUtils.isEmpty(psw)) {
                String rePsw = etRePsw.getText().toString();
                if (psw.equals(rePsw)) {
                    String sex = groupSex.getCheckedRadioButtonId() == R.id.radio_man ? "男" : "女";
                    String sign = etSign.getText().toString();
                    persenter.regist(nick, Md5Util.getMD5String(psw), sex, sign, userSelectGroups, photoPath);
                } else {
                    TipsUtil.toast(this, "两次输入密码不匹配");
                }
            } else {
                TipsUtil.toast(this, "密码不能为空");
            }
        } else {
            TipsUtil.toast(this, "昵称不能为空");
        }
    }

    @Override
    protected String getActivityTitle() {
        return "用户注册";
    }

    @OnClick({R.id.iv_icon, R.id.tv_tag_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_icon:
                //获取头像(图库，相机)
                showGetPicDialog();
                break;
            case R.id.tv_tag_select:
                //加载系统中群的列表，显示到popuwindow上，让用户选择(最多5个)
                showGroupSelectWindow(view);
                break;
        }
    }

    private GroupLoadUtil groupLoadUtil;

    private void showGroupSelectWindow(View view) {
        if (null == groupLoadUtil) {
            groupLoadUtil = new GroupLoadUtil();
            groupLoadUtil.setOnSelectListener(new GroupLoadUtil.OnSelectListener() {
                @Override
                public void onSelectResult(List<GroupInfo> list) {
                    tagLayout.removeAllViews();
                    userSelectGroups = list;
                    //添加当前选中的群组
                    int size;
                    if ((size = list.size()) > 0) {
                        for (int i = 0; i < size; i++) {
                            TextView tv = new TextView(RegistActivity.this);
                            tv.setTextSize(20);
                            tv.setTextColor(getResources().getColor(R.color.colorAccent));
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.leftMargin = 5;
                            tv.setText("#" + list.get(i).getGroupName() + "#");
                            tagLayout.addView(tv, lp);
                        }
                    }
                }
            });
        }
        groupLoadUtil.showSelectWindow(this, view, 5);
    }

    private GetPictureFragment getPictureFragment;

    private void showGetPicDialog() {
        if (null == getPictureFragment) {
            getPictureFragment = new GetPictureFragment();
            getPictureFragment.setOnGetPictureListener(new GetPictureFragment.OnGetPictureListener() {
                @Override
                public void onResult(Uri uri) {
                    photoPath = uri.getPath();
                    ivIcon.setImageURI(uri);
                }
            });
        }
        getPictureFragment.show(getSupportFragmentManager(), "pic");
    }

    @Override
    protected void onDestroy() {
        if (null != getPictureFragment) {
            getPictureFragment.clearTempFiles();
        }
        super.onDestroy();
    }
}
