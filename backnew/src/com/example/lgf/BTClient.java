package com.example.lgf;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BTClient implements Runnable {
    // 流连接
    private StreamConnection streamConn = null;

    // 接受数据字节流
    private byte[] acceptedByteArray = new byte[1024];
    // 读取（输入）流
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    /**
     * 主线程
     *
     * @param args
     */
    public static void main(String[] args) {
        new BTClient();
    }

    /**
     * 构造方法
     */
    public BTClient() {
        try {
            // 得到流连接通知，下面的UUID必须和手机客户端的UUID相一致。  
            UUID uuid = new UUID(Constant.UUID, false);
            DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
            System.out.println(LocalDevice.getLocalDevice().getBluetoothAddress());
            System.out.println(LocalDevice.getLocalDevice().getFriendlyName());
            System.out.println(LocalDevice.getLocalDevice().getDeviceClass());

//            String connectionURL = agent.selectService(uuid);
//            System.out.println(connectionURL);
            //"btspp://" + first.getBluetoothAddress() + ":1"


            streamConn = (StreamConnection)Connector.open("btspp://4CD1A1ACFACA:" + 8);
            System.out.println("ceshi");
        } catch (IOException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
        // 打开连接通道并读取流线程  
        new Thread(this).start();
    }

    @Override
    public void run() {
            try {
                inputStream = streamConn.openInputStream();
                outputStream = streamConn.openOutputStream();
                while(true) {
                    System.out.println("woshi");

                    outputStream.write("This message from client hhakk\r\n".getBytes(Constant.ENCODE));
                    System.out.println("woshi1");

                    outputStream.flush();
                    System.out.println("woshi2");

                    int len = inputStream.read(acceptedByteArray);
                    System.out.println("woshi3");
                    System.out.println(new String(acceptedByteArray, 0, len, Constant.ENCODE));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }
} 