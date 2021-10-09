package com.jg.localnotifcation.jg_local_notification;

import android.app.Notification ;
import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.content.BroadcastReceiver ;
import android.content.Context ;
import android.content.Intent ;
import android.os.Bundle;

public class MyNotificationPublisher extends BroadcastReceiver {

    public void onReceive (Context context , Intent intent) {
        Bundle bundle = intent.getExtras();
        if(context !=null && bundle!=null && bundle.containsKey(Constants.SCHEDULE_NOTIFICATION_PAYLOAD)){

            NotificationDataModel myNotificationDataModel=(NotificationDataModel)intent.getSerializableExtra(Constants.SCHEDULE_NOTIFICATION_PAYLOAD);
            if(myNotificationDataModel!=null){
                NotificationEngine notificationEngine =new NotificationEngine();
                notificationEngine.show(myNotificationDataModel,context);
            }
        }
    }
}
