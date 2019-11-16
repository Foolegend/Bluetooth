package com.example.lgf.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.example.lgf.bluetooth.util.Constant;

import java.io.IOException;
import java.util.UUID;

/**
 * 监听连接申请的线程
 */
public class ServerSocketThread extends Thread {
    private static final String NAME = "BlueToothClass";
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTTION_UUID);

    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter mBluetoothAdapter;
    private final Handler msgHandler;
    private SocketStreamManagerThread soketStreamManager;

    public ServerSocketThread(BluetoothAdapter adapter, Handler handler) {
        // 使用一个临时对象，该对象稍后被分配给mmServerSocket，因为mmServerSocket是最终的
        mBluetoothAdapter = adapter;
        msgHandler = handler;
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID是应用程序的UUID，客户端代码使用相同的UUID
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket;
        //持续监听，直到出现异常或返回socket
        while (true) {
            try {
                msgHandler.sendEmptyMessage(Constant.MSG_START_LISTENING);
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                msgHandler.sendMessage(msgHandler.obtainMessage(Constant.MSG_ERROR, e));
                break;
            }
            // 如果一个连接被接受
            if (socket != null) {
                // 在单独的线程中完成管理连接的工作
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                    msgHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        //只支持同时处理一个连接
        if( soketStreamManager != null) {
            soketStreamManager.cancel();
        }
        msgHandler.sendEmptyMessage(Constant.MSG_GOT_A_CLINET);
        soketStreamManager = new SocketStreamManagerThread(socket, msgHandler);
        soketStreamManager.start();
    }

    /**
     * 取消监听socket，使此线程关闭
     */
    public void cancel() {
        try {
            mmServerSocket.close();
            msgHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
        } catch (IOException e) { }
    }

    public void sendData(byte[] data) {
        if( soketStreamManager !=null){
            soketStreamManager.write(data);
        }
    }
}