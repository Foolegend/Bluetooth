package com.example.lgf.bluetooth.server.handler;

import android.os.Handler;
import android.os.Message;

import com.example.lgf.bluetooth.MainActivity;
import com.example.lgf.bluetooth.util.Constant;
import com.example.lgf.bluetooth.util.Utils;

public class ServerMsgHandler extends Handler {
    private MainActivity mainActivity;
    public ServerMsgHandler(MainActivity mainActivity){
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
            }
        }
    }