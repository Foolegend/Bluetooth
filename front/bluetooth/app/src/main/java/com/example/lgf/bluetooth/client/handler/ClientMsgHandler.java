package com.example.lgf.bluetooth.client.handler;

import android.os.Handler;
import android.os.Message;

import com.example.lgf.bluetooth.MainActivity;
import com.example.lgf.bluetooth.util.Constant;
import com.example.lgf.bluetooth.util.Utils;

public class ClientMsgHandler extends Handler {
    private MainActivity mainActivity;
    public ClientMsgHandler(MainActivity mainActivity){
        this.mainActivity = mainActivity;

    }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case Constant.MSG_GOT_DATA:
                    Utils.showToast(mainActivity,"data:" + String.valueOf(message.obj));
                break;
                case Constant.MSG_ERROR:
                    Utils.showToast(mainActivity,"error:" + String.valueOf(message.obj));
                break;
                case Constant.MSG_CONNECTED_TO_SERVER:
                    Utils.showToast(mainActivity,"连接到服务端");
                break;
                case Constant.MSG_GOT_A_CLINET:
                    Utils.showToast(mainActivity,"找到服务端");
                break;
            }
        }
    }