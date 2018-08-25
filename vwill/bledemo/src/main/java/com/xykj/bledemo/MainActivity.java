package com.xykj.bledemo;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 用来查找设备，点击设备后跳转到控制界面
 */
public class MainActivity extends Activity implements
        AdapterView.OnItemClickListener {
    public static final int REQUEST_PERMISSION = 1;
    public static final int REQUEST_OPEN = 2;

    private BluetoothAdapter adapter;

    private ArrayAdapter<String> listAdapter;
    private List<String> deviceNames = new ArrayList<String>();
    private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Android4.3以上版本获取蓝牙管理器方式(android-18)
        adapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE))
                .getAdapter();
        ListView listView = (ListView) findViewById(R.id.list_devices);
        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, deviceNames);
        listView.setAdapter(listAdapter);
        // 获取权限
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        } else {
            // 开始扫描
            scan();
        }
        // 设备的点击事件
        listView.setOnItemClickListener(this);
    }

    /**
     * 6.0以上需要动态申请获取模拟定位权限才可以操作蓝牙
     */
    @SuppressLint("NewApi")
    private void checkPermission() {
        int check = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (check == PackageManager.PERMISSION_GRANTED) {
            // 开始扫描
            scan();
        } else {
            // 申请权限
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION);
        }
    }

    public void scan() {
        // 判断是否打开了蓝牙
        if (adapter.isEnabled()) {
            // 开始扫描
            startScan();
        } else {
            startActivityForResult(new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_OPEN);
        }
    }

    public void startScan() {
        adapter.startDiscovery();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(
                BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scan();
        } else {
            Toast.makeText(this, "未获取到地理权限，无法请求扫描设备", Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN && resultCode == RESULT_OK) {
            startScan();
        } else {
            Toast.makeText(this, "蓝牙打开失败，请手动打开", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 1.搜索设备
            // 2.判断蓝牙打开
            Log.e("TAG", "-----------------广播：" + intent.getAction());
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                Log.e("TAG", "---------------蓝牙状态改变" + adapter.getState());
                if (adapter.getState() == BluetoothAdapter.STATE_ON) // 4种状态
                {
                    startScan();
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceNames.add(device.getName() == null ? "匿名" : device
                        .getName());
                devices.add(device);
                listAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        BluetoothDevice device = devices.get(position);
        if (!device.getName().startsWith("KQX")) {
            Toast.makeText(this, "选取的并不是智能灯泡", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra("device", device);
        startActivity(intent);
    }
}