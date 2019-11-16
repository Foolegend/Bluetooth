package com.example.lgf.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lgf.bluetooth.base.BlueToothController;
import com.example.lgf.bluetooth.base.DeviceAdapter;
import com.example.lgf.bluetooth.client.ClientThread;
import com.example.lgf.bluetooth.server.ServerSocketThread;
import com.example.lgf.bluetooth.server.handler.MsgHandler;
import com.example.lgf.bluetooth.util.Utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<BluetoothDevice> allDeviceList = new ArrayList<BluetoothDevice>();
    private List<BluetoothDevice> hasBondedDeviceList = new ArrayList<BluetoothDevice>();
    private BlueToothController blueToothController = new BlueToothController();
    private Handler msgHandler = new MsgHandler(this);

    public static final int REQUEST_CODE = 0;
    private DeviceAdapter deviceAdapter;
    private ListView bodyLayoutView;

    private ServerSocketThread server;
    private ClientThread client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        registerBluetoothReceiver();
        //软件运行时直接申请打开蓝牙
        blueToothController.turnOnBlueTooth(this, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Utils.showToast(this,"打开成功");
        }else{
            Utils.showToast(this,"打开失败");
        }
    }

    //注册广播监听搜索结果
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                //setProgressBarIndeterminateVisibility(true);
                //初始化数据列表
                allDeviceList.clear();
                deviceAdapter.notifyDataSetChanged();
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                //setProgressBarIndeterminateVisibility(false);
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个添加一个
                allDeviceList.add(device);
                deviceAdapter.notifyDataSetChanged();

            } else if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                //此处作用待细查
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                if(scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                    setProgressBarIndeterminateVisibility(true);
                } else {
                    setProgressBarIndeterminateVisibility(false);
                }

            } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(remoteDevice == null) {
                    Utils.showToast(MainActivity.this,"无设备");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0);
                if(status == BluetoothDevice.BOND_BONDED) {
                    Utils.showToast(MainActivity.this,"已绑定" + remoteDevice.getName());
                } else if(status == BluetoothDevice.BOND_BONDING) {
                    Utils.showToast(MainActivity.this,"正在绑定" + remoteDevice.getName());
                } else if(status == BluetoothDevice.BOND_NONE) {
                    Utils.showToast(MainActivity.this,"未绑定" + remoteDevice.getName());
                }
            }
        }
    };

    //初始化用户界面
    private void initUI() {
        bodyLayoutView = findViewById(R.id.device_list);
        deviceAdapter = new DeviceAdapter(allDeviceList, this);
        bodyLayoutView.setAdapter(deviceAdapter);
        bodyLayoutView.setOnItemClickListener(bondDeviceClick);
    }

    private AdapterView.OnItemClickListener bondDeviceClick = new AdapterView.OnItemClickListener() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = allDeviceList.get(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                device.createBond();
            }
        }
    };

    private AdapterView.OnItemClickListener bondedDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = hasBondedDeviceList.get(i);
            if (client != null) {
                client.cancel();
            }
            client = new ClientThread(device, blueToothController.getAdapter(), msgHandler);
            client.start();
        }
    };

    private void registerBluetoothReceiver(){
        IntentFilter filter = new IntentFilter();
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(receiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.enable_visibility) {
            blueToothController.enableVisibily(this);
        } else if (id == R.id.find_device) {
            //查找设备
            deviceAdapter.refresh(allDeviceList);
            blueToothController.findDevice();
            bodyLayoutView.setOnItemClickListener(bondDeviceClick);
        } else if (id == R.id.bonded_device) {
            //查看已绑定设备
            hasBondedDeviceList = blueToothController.getBondedDeviceList();
            deviceAdapter.refresh(hasBondedDeviceList);
            bodyLayoutView.setOnItemClickListener(bondedDeviceClick);
        } else if( id == R.id.listening) {
            if( server != null) {
                server.cancel();
            }
            server = new ServerSocketThread(blueToothController.getAdapter(), msgHandler);
            server.start();
        } else if( id == R.id.stop_listening) {
            if( server != null) {
                server.cancel();
            }
        } else if( id == R.id.disconnect) {
            if( client != null) {
                client.cancel();
            }
        } else if( id == R.id.say_hello) {
            say("Hello");
        }
        else if( id == R.id.say_hi) {
            say("Hi");
        }

        return super.onOptionsItemSelected(item);
    }

    private void say(String word) {
        if (server != null) {
            try {
                server.sendData(word.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        else if( client != null) {
            try {
                client.sendData(word.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }
}
