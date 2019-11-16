package com.example.lgf.bluetooth.base;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙适配器
 * Created by liguofa on 19-11-10.
 */

public class BlueToothController {
    private BluetoothAdapter mAdapter;
    public BlueToothController(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    public boolean isSupportBlueTooth(){
        return this.mAdapter != null;
    }

    public boolean getBlueToothStatus(){
        if(mAdapter != null){
            return mAdapter.isEnabled();
        }
        return false;
    }
    public void turnOnBlueTooth(Activity activity, int requestCode){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    public void turnOffBlueTooth() {
        mAdapter.disable();
    }

    //打开蓝牙可见性
    public void enableVisibily(Context context){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(intent);
    }

    //查找设备
    public void findDevice(){
        if(mAdapter != null){
            mAdapter.startDiscovery();
        }
    }

    /**
     * 获取已绑定设备
     */
    public List<BluetoothDevice> getBondedDeviceList(){
        return new ArrayList<>(mAdapter.getBondedDevices());
    }
}
