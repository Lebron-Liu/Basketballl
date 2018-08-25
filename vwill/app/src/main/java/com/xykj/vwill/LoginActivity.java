package com.xykj.vwill;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.xykj.bean.User;
import com.xykj.persenter.LoginPersenter;
import com.xykj.smssdk.SMSCallback;
import com.xykj.smssdk.SMSSdk;
import com.xykj.utils.Common;
import com.xykj.view.BaseActivity;
import com.xykj.view.LoginView;
import com.xyy.net.DownloadRequestItem;
import com.xyy.net.FileRequestItem;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.StringRequestItem;
import com.xyy.net.imp.Callback;
import com.xyy.utils.TipsUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity<LoginPersenter> implements LoginView {
    public static final int PLATFORM_MSG = 1;
    public static final int PLATFORM_QQ = 2;
    public static final int PLATFORM_SINA = 3;
    private int platform;

    @BindView(R.id.et_name)
    TextInputEditText etName;
    @BindView(R.id.et_psw)
    TextInputEditText etPsw;
    @BindView(R.id.login_main_layout)
    LinearLayout loginMainLayout;

    @Override
    protected int getType() {
        return TYPE_BACK;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void showUser(User u) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        //如果是QQ或者新浪的时候，第一次登录从QQ服务器或者新浪的服务器得到openid提交到VWill服务端中，VWill服务端并没有用户的昵称、头像、性别
        //所以如果使用了QQ、新浪等第三方平台登录，需要再去获取用户的基本信息，提交到我们的VWill服务器上
        //如果没有用户信息则会有默认昵称，格式为“用户+ID"
        if (u.getNick().equals("用户" + u.getId())) {
            //默认昵称，从第三方平台上获取信息，提交到VWill服务器上
            switch (platform) {
                case PLATFORM_SINA:
                    //从新浪服务器上获取
                    loadUserInfoFromSina();
                    break;
            }
        } else {
            backResult(u);
        }
    }

    private void backResult(User u) {
        //记录到App中(全局变量)
        VWillApp app = (VWillApp) getApplication();
        app.setLoginUser(u);
        //启动VWillService登录ApacheMina以便于接收即时消息
        Intent it = new Intent(this, VWillService.class);
        startService(it);
        TipsUtil.toast(this, "登录成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册短信验证的回调
        SMSSdk.getInstance().registerEventHandler(smsCallback);
    }

    //短信获取回调
    private SMSCallback smsCallback = new SMSCallback() {
        @Override
        public void afterEvent(int i, Object o) {
            //子线程中
            switch (i) {
                case SMSSdk.EVENT_GET_OK:
                    //获取验证码成功
                    TipsUtil.log("获取成功");
                    break;
                case SMSSdk.EVENT_GET_FAIL:
                    //获取验证码失败
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TipsUtil.toast(LoginActivity.this, "验证码获取失败");
                        }
                    });
                    break;
                case SMSSdk.EVENT_VERIFY_OK:
                    //验证成功
                    //停止倒计时
                    stopTimeTick();
                    //登录
                    persenter.loginByPhone(phone);
                    break;
                case SMSSdk.EVENT_VERIFY_FAIL:
                    //验证失败
                    break;
            }
        }
    };

    //倒计时线程
    private Thread timeThread;

    private void startTimeTick() {
        if (timeThread != null && timeThread.isAlive()) {
            timeThread.interrupt();
        }
        timeThread = new Thread() {
            @Override
            public void run() {
                //倒计时120秒
                int max = 120;
                try {
                    while (max > 0) {
                        max--;
                        mHandler.obtainMessage(1, max).sendToTarget();
                        Thread.sleep(1000);
                    }
                    //正常结束
                } catch (InterruptedException e) {
                    //倒计时被停止
                }
                mHandler.sendEmptyMessage(0);
            }
        };
        timeThread.start();
    }

    private void stopTimeTick() {
        if (timeThread != null) {
            timeThread.interrupt();
            timeThread = null;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (btnGetCode.isEnabled()) {
                        btnGetCode.setEnabled(false);
                    }
                    int time = (int) msg.obj;
                    btnGetCode.setText("剩余" + time + "秒");
                    break;
                case 0:
                    btnGetCode.setEnabled(true);
                    btnGetCode.setText("获取验证码");
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        SMSSdk.getInstance().unregisterEventHandler(smsCallback);
        super.onDestroy();
    }

    @OnClick({R.id.btn_login, R.id.tv_login_by_msg, R.id.tv_login_by_wx, R.id.tv_login_by_qq, R.id.tv_login_by_wb, R.id.tv_to_regist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                platform = 0;
                String name = etName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    String psw = etPsw.getText().toString();
                    if (!TextUtils.isEmpty(psw)) {
                        persenter.loginByAccount(name, psw);
                    } else {
                        TipsUtil.toast(this, "密码不能为空");
                    }
                } else {
                    TipsUtil.toast(this, "用户名或者账号不能为空");
                }
                break;
            case R.id.tv_login_by_msg:
                platform = PLATFORM_MSG;
                //获取短信验证码
                showPhoneMsgGetWindow();
                break;
            case R.id.tv_login_by_wx:
                break;
            case R.id.tv_login_by_qq:
                platform = PLATFORM_QQ;
                break;
            case R.id.tv_login_by_wb:
                platform = PLATFORM_SINA;
                //新浪微博
                loginBySinaWb();
                break;
            case R.id.tv_to_regist:
                Intent it = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(it);
                break;
        }
    }

    //===========新浪微博登录开始=================//
    private SsoHandler ssoHandler;
    private Oauth2AccessToken mOauth2AccessToken;

    //授权得到uid
    private void loginBySinaWb() {
        if (null == ssoHandler) {
            ssoHandler = new SsoHandler(this);
        }
        ssoHandler.authorize(new SelfWbAuthListener());
    }

    //授权结果监听器
    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {

        @Override
        public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
            //授权成功
            mOauth2AccessToken = oauth2AccessToken;
            //基于uid登录到VWill服务器，检测该uid是否已经有记录
            persenter.loginByThirdPlatform(oauth2AccessToken.getUid());
        }

        @Override
        public void cancel() {
            //取消授权
        }

        @Override
        public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
            //授权失败
        }
    }

    //如果没有则从新浪微博加载信息（昵称、头像、性别）
    private void loadUserInfoFromSina() {
        NetManager.getInstance().execute(new RequestItem.Builder()
                .url("https://api.weibo.com/2/users/show.json?uid=" + mOauth2AccessToken.getUid() + "&access_token=" + mOauth2AccessToken.getToken())
                .build(), new Callback<JSONObject>() {
            @Override
            public JSONObject changeData(ResponceItem responce) {
                String json = responce.getString();
                try {
                    JSONObject obj = new JSONObject(json);
                    return obj;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onResult(JSONObject result) {
                if (result != null) {
                    String headUrl = result.optString("profile_image_url");
                    String sex = "m".equals(result.optString("gender")) ? "男" : "女";
                    String name = result.optString("name");
                    //下载头像更新信息
                    downloadPhoto(name, sex, headUrl);
                }
            }
        });
    }


    //如果已经有信息，则将信息记录到App中即可登录结束

    //===========新浪微博登录结束=================//

    //===========从第三方平台加载用户基本信息，同时下载头像，然后提交到VWill服务器,开始======//
    private void downloadPhoto(final String nick, final String sex, String url) {
        final String savePath = getExternalCacheDir() + "/temp/" + System.currentTimeMillis() + ".jpg";
        File f = new File(savePath);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        NetManager.getInstance().execute(new DownloadRequestItem.Builder()
                .url(url)
                .savePath(savePath)
                .build(), new Callback<Boolean>() {
            @Override
            public Boolean changeData(ResponceItem responce) {
                return responce.getCode() == 200;
            }

            @Override
            public void onResult(Boolean result) {
                if (result) {
                    //提交信息到服务器端
                    uploadUserInfo(nick,sex,new File(savePath));
                }
            }
        });
    }

    private void uploadUserInfo(String nick, String sex, File file) {
        NetManager.getInstance().execute(new FileRequestItem.Builder()
                        .url(Common.URL_UPDATE_USER_INFO)
                        .addHead("token", Common.TOKEN)
                        .addStringParam("nick", nick)
                        .addStringParam("sex", sex)
                        .addFileParam("photo", file)
                        .build(),
                new Callback<User>() {
                    @Override
                    public User changeData(ResponceItem responce) {
                        String json = responce.getString();
                        if (!json.startsWith("{result")) {
                            User u = (User) JSON.parseObject(json, User.class);
                            return u;
                        }
                        return null;
                    }

                    @Override
                    public void onResult(User result) {
                        if (result != null) {
                            backResult(result);
                        }
                    }
                });
    }
    //===========从第三方平台加载用户基本信息，同时下载头像，然后提交到VWill服务器,结束======//

    private PopupWindow popupWindow;
    private EditText etPhone, etCode;
    private TextView btnGetCode;
    //手机号
    private String phone;

    private void showPhoneMsgGetWindow() {
        if (null == popupWindow) {
            View layout = getLayoutInflater().inflate(R.layout.phone_verify_layout, null);
            //获取验证码按钮的监听
            btnGetCode = layout.findViewById(R.id.btn_get_code);
            btnGetCode.setOnClickListener(onWindowViewClick);
            //提交按钮的监听
            layout.findViewById(R.id.btn_submit).setOnClickListener(onWindowViewClick);
            etPhone = layout.findViewById(R.id.et_phone);
            etCode = layout.findViewById(R.id.et_code);
            popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable());
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    stopTimeTick();
                }
            });
        }
        popupWindow.showAtLocation(loginMainLayout, Gravity.LEFT, 0, 0);
    }

    private View.OnClickListener onWindowViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_get_code:
                    //获取用户手机号码
                    phone = etPhone.getText().toString();
                    if (!TextUtils.isEmpty(phone)) {
                        //获取手机的验证码
                        SMSSdk.getInstance().getVerificationCode(phone);
                        //倒计时120秒之后可以重新获取
                        startTimeTick();
                    }
                    break;
                case R.id.btn_submit:
                    //获取用户输入的验证码
                    String code = etCode.getText().toString();
                    //验证手机号和验证码是否一样
                    SMSSdk.getInstance().submitVerificationCode(phone, code);
                    break;
            }
        }
    };

    @Override
    protected String getActivityTitle() {
        return "用户登录";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (platform){
            case PLATFORM_SINA:
                ssoHandler.authorizeCallBack(requestCode,resultCode,data);
                break;
        }
    }
}
