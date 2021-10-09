package com.jg.localnotifcation.jg_local_notification;

/*This Broadcast Receiver is used to trigger the scheduled notifications */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ScheduledNotificationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (context != null && bundle != null) {
            NotificationDataModel myNotificationDataModel = new NotificationDataModel(bundle);
            if (myNotificationDataModel != null) {
                NotificationEngine notificationEngine = new NotificationEngine();
                notificationEngine.show(myNotificationDataModel, context);
            }
        }
    }
}
