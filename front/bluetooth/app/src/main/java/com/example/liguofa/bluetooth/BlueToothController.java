package com.example.liguofa.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * 蓝牙适配器
 * Created by liguofa on 19-11-10.
 */

public class BlueToothController {
    private BluetoothAdapter mAdapter;
    public BlueToothController(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    public boolean isSupportBlueTooth(){
        return this.mAdapter != null;
    }

    public boolean getBlueToothStatus(){
        if(mAdapter != null){
            return mAdapter.enable();
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
}
