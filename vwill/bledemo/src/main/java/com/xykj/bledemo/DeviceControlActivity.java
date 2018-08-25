package com.xykj.bledemo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/4/12.
 */

public class DeviceControlActivity extends Activity implements BleConnUtils.OnReaderDataReceiverListener {
    BluetoothDevice device;
    BleConnUtils utils;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = getIntent().getParcelableExtra("device");
        if (device == null) {
            Toast.makeText(this, "传递设备为空", Toast.LENGTH_SHORT).show();
            finish();
        }
        utils = new BleConnUtils(device, this);
        utils.setOnReaderDataReceiverListener(this);
        ColorImageView colorImageView = new ColorImageView(this);
        colorImageView.setOnColorSelectoedListener(new ColorImageView.OnColorSelectoedListener() {
            @Override
            public void onColorSelectoed(int color) {
                utils.sendMessage(ProtocolUtils.sendRGB(color));
            }
        });
        setContentView(colorImageView);
    }

    @Override
    public void onReaderDataReceiver(byte[] buf) {
        for (byte b : buf) {
            Log.e("TAG", "----------------" + b);
        }
    }

    @Override
    public void onGattConn() {
        utils.sendMessage(ProtocolUtils.sendPasswrod());
    }
}