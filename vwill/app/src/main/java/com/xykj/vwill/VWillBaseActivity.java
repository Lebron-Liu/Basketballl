package com.xykj.vwill;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xykj.vwill.R;

import butterknife.ButterKnife;

public abstract class VWillBaseActivity extends AppCompatActivity {
    //只有返回
    protected static final int TYPE_BACK = 1;
    //有取消和确认
    protected static final int TYPE_CANCEL_SURE = 2;
    // 有返回和菜单
    protected static final int TYPE_BACK_MENU = 3;
    //返回按钮和确认
    protected static final int TYPE_BACK_SURE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base);
        //初始化标题
        initTitle(getType());
        //添加内容
        LinearLayout baseLayout = findViewById(R.id.base_layout);
        View contentLayout = LayoutInflater.from(this).inflate(getLayout(), null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        baseLayout.addView(contentLayout, lp);
        initLayout();
    }

    protected void initLayout() {
        ButterKnife.bind(this);
    }

    private void initTitle(int type) {
        if (type > 0) {
            ViewStub stub = findViewById(R.id.title_view);
            stub.setVisibility(View.VISIBLE);
            //中间的标题文本
            ((TextView) findViewById(R.id.tv_title_center)).setText(getActivityTitle());
            //左边的视图的点击处理
            TextView tvLeft = findViewById(R.id.tv_title_left);
            tvLeft.setOnClickListener(onTitleViewClick);
            TextView tvRight = findViewById(R.id.tv_title_right);
            switch (type) {
                case TYPE_BACK:
                    tvLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
                    break;
                case TYPE_CANCEL_SURE:
                    tvLeft.setText(getTitleLeftText());
                    tvRight.setText(getTitleRightText());
                    tvRight.setOnClickListener(onTitleViewClick);
                    break;
                case TYPE_BACK_MENU:
                    tvLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
                    tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_menu, 0);
                    tvRight.setOnClickListener(onTitleViewClick);
                    break;
                case TYPE_BACK_SURE:
                    tvLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
                    tvRight.setText(getTitleRightText());
                    tvRight.setOnClickListener(onTitleViewClick);
                    break;
            }
        }
    }

    //获取标题类型
    protected abstract int getType();

    //页面布局的加载
    protected abstract int getLayout();

    protected void onTitleRightViewClick(View v) {

    }

    private View.OnClickListener onTitleViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_title_left:
                    finish();
                    break;
                case R.id.tv_title_right:
                    onTitleRightViewClick(view);
                    break;
            }
        }
    };

    protected String getActivityTitle() {
        return getTitle().toString();
    }

    protected String getTitleLeftText() {
        return "取消";
    }

    protected String getTitleRightText() {
        return "确定";
    }
}
