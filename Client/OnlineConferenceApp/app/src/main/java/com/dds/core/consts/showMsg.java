package com.dds.core.consts;

import android.content.Context;
import android.widget.Toast;

public class showMsg {

    public static void showShort(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static void showLong(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
