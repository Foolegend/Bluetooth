package com.example.liguofa.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private BlueToothController blueToothController = new BlueToothController();
    private Toast toast;
    private final int REQUEST_CODE = 1;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch(state){
                case BluetoothAdapter.STATE_OFF:
                    showToast("STATE_OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
                    showToast("STATE_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    showToast("STATE_TURNING_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    showToast("STATE_TURNING_OFF");
                    break;
                default:
                    showToast("Unknown State");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void isSupportBlueTooth(View view){
       boolean isSupport = blueToothController.isSupportBlueTooth();
       showToast("Support BlueTooth:" + isSupport);
    }

    public void isBlueToothEnable(View view){
        boolean isEnable = blueToothController.getBlueToothStatus();
        showToast("Support Status:" + isEnable);
    }

    public void turnOnBlueTooth(View view){
        blueToothController.turnOnBlueTooth(this, REQUEST_CODE);
    }

    public void turnOffBlueTooth(View view){
        blueToothController.turnOffBlueTooth();
    }
    private void showToast(String text){
        if(toast == null){
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }else{
            toast.setText(text);
        }
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_OK){
            showToast("打开成功");
        }else{
            showToast("打开失败");
        }
    }
}
