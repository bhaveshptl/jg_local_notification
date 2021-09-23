package com.jg.localnotifcation.jg_local_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;


public class MyNotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            MySharedPreferences.setBoolValue(context,Constants.IS_PUSH_NOTIFICATION_DISMISSED,true);
            Map<String,Object> data=new HashMap<>();
            data.put("payload",null);
            data.put("bNotifDismissed",true);
            if(JgLocalNotificationPlugin.eventSink!=null){
                JgLocalNotificationPlugin.eventSink.success(data);
            }

        }catch (Exception e){
        }
    }
}
