package com.example.lgf.bluetooth.client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.example.lgf.bluetooth.util.Constant;

import java.io.IOException;
import java.util.UUID;


public class ClientThread extends Thread {
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTTION_UUID);
    private final BluetoothSocket clientSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private final Handler clientMsgHandler;
    private ClientSocketStream clientSocketStream;

    public ClientThread(BluetoothDevice device, BluetoothAdapter adapter, Handler handler) {
        // U将一个临时对象分配给mmSocket，因为mmSocket是最终的
        BluetoothSocket tmp = null;
        mBluetoothAdapter = adapter;
        clientMsgHandler = handler;
        // 用BluetoothSocket连接到给定的蓝牙设备
        try {
            // MY_UUID是应用程序的UUID，客户端代码使用相同的UUID
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        clientSocket = tmp;
    }

    public void run() {
        // 搜索占用资源大，关掉提高速度
        mBluetoothAdapter.cancelDiscovery();

        try {
            // 通过socket连接设备，阻塞运行直到成功或抛出异常时
            clientSocket.connect();
        } catch (Exception connectException) {
            clientMsgHandler.sendMessage(clientMsgHandler.obtainMessage(Constant.MSG_ERROR, connectException));
            // 如果无法连接则关闭socket并退出
            try {
                clientSocket.close();
            } catch (IOException closeException) { }
            return;
        }
        // 在单独的线程中完成管理连接的工作
        manageConnectedSocket(clientSocket);
    }

    private void manageConnectedSocket(BluetoothSocket clientSocket) {
        clientMsgHandler.sendEmptyMessage(Constant.MSG_CONNECTED_TO_SERVER);
        clientSocketStream = new ClientSocketStream(clientSocket, clientMsgHandler);
        clientSocketStream.start();
    }

    /**
     * 取消正在进行的连接并关闭socket
     */
    public void cancel() {
        try {
            clientSocket.close();
        } catch (IOException e) { }
    }

    /**
     * 发送数据
     */
    public void sendData(byte[] data) {
        if( clientSocketStream !=null){
            clientSocketStream.write(data);
        }
    }
}