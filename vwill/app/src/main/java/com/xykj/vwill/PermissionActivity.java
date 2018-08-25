package com.xykj.vwill;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import java.util.Arrays;
import java.util.List;

/**
 * 申请授权的Activity（1、可以进入设置打开或者关闭权限，2、直接使用授权申请方法来申请）
 * github PermissionGrant
 */
public class PermissionActivity extends Activity {
    private static final int REQUEST_SETTINGS = 1;
    private static final int REQUEST_PERMISSION = 2;
    private String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it = getIntent();
        if (it == null || !it.hasExtra("permissions")) {
            finish();
            return;
        }
        permissions = it.getStringArrayExtra("permissions");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_permission);
        findViewById(R.id.btn_setting).setOnClickListener(btnClick);
        findViewById(R.id.btn_grant).setOnClickListener(btnClick);
    }

    /**
     * 启动授权界面
     *
     * @param context
     * @param requestCode
     * @param permissions
     */
    public static void startPermissionActivity(Activity context, int requestCode, String... permissions) {
        Intent it = new Intent(context, PermissionActivity.class);
        it.putExtra("permissions", permissions);
        context.startActivityForResult(it, requestCode);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_setting:
                    //进入设置打开权限
                    Intent it = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    //配置跳转要查看的应用的包名 packege:com.xykj.filemanager
                    it.setData(Uri.parse("package:" + getApplication().getPackageName()));
                    startActivityForResult(it, REQUEST_SETTINGS);
                    break;
                case R.id.btn_grant:
                    //使用申请授权方法
                    ActivityCompat.requestPermissions(PermissionActivity.this, permissions, REQUEST_PERMISSION);
                    break;
            }
        }
    };

    //授权结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            int deniedIndex = getFirstDeniedIndex(grantResults);
            if (deniedIndex != -1) {
                //用户拒绝
                //检测是否有必要跟用户说明为什么要申请这个权限，如果拒绝了会有什么结果
                if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionActivity.this, permissions[deniedIndex])) {
                    //解释一下
                    AlertDialog.Builder b = new AlertDialog.Builder(PermissionActivity.this);
                    b.setTitle("警告")
                            .setMessage("不授权将影响应用的正常使用,是否需要重新授权?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(PermissionActivity.this, PermissionActivity.this.permissions, REQUEST_PERMISSION);
                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    b.show();
                }
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private int getFirstDeniedIndex(int[] grantResults) {
        int len = grantResults.length;
        for (int i = 0; i < len; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 检测是否授权
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean isGrantedPermission(Context context, String... permissions) {
        return isGrantedPermission(context, Arrays.asList(permissions));
    }

    /**
     * 检测是否授权
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean isGrantedPermission(Context context, List<String> permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int size = permissions.size();
        for (int i = 0; i < size; i++) {
            String name = permissions.get(i);
            //一个个的检测授权情况，如果发现了其中某一个未授权，返回false表示检测授权失败
            int code = ActivityCompat.checkSelfPermission(context, name);
            if (code == PackageManager.PERMISSION_DENIED) {
                return false;
            }
            String op = AppOpsManagerCompat.permissionToOp(name);
            if (TextUtils.isEmpty(op)) {
                continue;
            }
            code = AppOpsManagerCompat.noteProxyOp(context, op, context.getApplicationContext().getPackageName());
            if (code != AppOpsManagerCompat.MODE_ALLOWED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS) {
            if (isGrantedPermission(PermissionActivity.this, permissions)) {
                setResult(RESULT_OK);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    }
}
