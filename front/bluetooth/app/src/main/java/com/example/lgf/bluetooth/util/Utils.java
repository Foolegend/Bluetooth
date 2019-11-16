package com.example.lgf.bluetooth.util;

import android.widget.Toast;

import com.example.lgf.bluetooth.MainActivity;

/**
 * Created by liguofa on 19-11-13.
 */

public class Utils {
    private static Toast toast;

    public static void showToast(MainActivity mainActivity, String text){
        if(toast == null){
            toast = Toast.makeText(mainActivity, text, Toast.LENGTH_SHORT);
        }else{
            toast.setText(text);
        }
        toast.show();
    }
}
