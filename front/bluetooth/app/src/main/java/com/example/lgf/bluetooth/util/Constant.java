package com.example.lgf.bluetooth.util;

/**
 * 给定状态参数常量
 */
public interface Constant {
     String CONNECTTION_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    /**
     * 开始监听
     */
     int MSG_START_LISTENING = 1;

    /**
     * 结束监听
     */
     int MSG_FINISH_LISTENING = 2;

    /**
     * 有客户端连接
     */
     int MSG_GOT_A_CLINET = 3;

    /**
     * 连接到服务器
     */
     int MSG_CONNECTED_TO_SERVER = 4;

    /**
     * 获取到数据
     */
     int MSG_GOT_DATA = 5;

    /**
     * 出错
     */
     int MSG_ERROR = -1;

     String ENCODE = "GBK";
    
}
