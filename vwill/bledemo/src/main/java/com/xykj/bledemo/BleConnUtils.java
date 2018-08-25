package com.xykj.bledemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by Administrator on 2017/4/12.
 */

public class BleConnUtils {

    //连接设备
    BluetoothDevice device;
    BluetoothAdapter adapter;
    BluetoothGatt gatt;

    BluetoothGattCharacteristic writer;
    BluetoothGattCharacteristic reader;

    public BleConnUtils(BluetoothDevice device, Context context) {
        this.device = device;
        Log.e("TAG", "--------------构造方法");
        adapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        gatt = device.connectGatt(context, false, callback);
        gatt.connect();
    }


    /**
     * 发送数据
     *
     * @param buf
     */
    public void sendMessage(byte[] buf) {
        if (writer == null)
            return;
        writer.setValue(buf);
        gatt.writeCharacteristic(writer);
    }

    private final android.bluetooth.BluetoothGattCallback callback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e("TAG", "------------连接" + device.getName());
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.e("TAG", "------------获取服务");
            //开始找服务
//            List<BluetoothGattService> services = gatt.getServices();
//            for (BluetoothGattService service : services) {
//                Log.e("TAG", "------------" + service.getUuid());
//            }
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getServices().get(2);
                writer = service.getCharacteristics().get(0);
                reader = service.getCharacteristics().get(1);
                //打开读取开关
                for (BluetoothGattDescriptor descriptor : reader.getDescriptors()) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                }
                gatt.setCharacteristicNotification(reader, true);
            }

//            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
//            for (BluetoothGattCharacteristic characteristic : characteristics) {
//                Log.e("TAG", "------特征值：" + characteristic.getUuid());
//            }
            if (onReaderDataReceiverListener != null)
                onReaderDataReceiverListener.onGattConn();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //在这里读取
            byte[] value = characteristic.getValue();
            if (onReaderDataReceiverListener != null)
                onReaderDataReceiverListener.onReaderDataReceiver(value);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    public void setOnReaderDataReceiverListener(OnReaderDataReceiverListener onReaderDataReceiverListener) {
        this.onReaderDataReceiverListener = onReaderDataReceiverListener;
    }

    OnReaderDataReceiverListener onReaderDataReceiverListener;

    public interface OnReaderDataReceiverListener {
        void onReaderDataReceiver(byte[] buf);

        void onGattConn();
    }
}
