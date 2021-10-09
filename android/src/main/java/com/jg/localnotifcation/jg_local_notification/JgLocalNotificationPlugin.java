package com.jg.localnotifcation.jg_local_notification;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import java.util.HashMap;
import java.util.Map;


/**
 * JgLocalNotificationPlugin
 */
public class JgLocalNotificationPlugin implements FlutterPlugin, MethodCallHandler, PluginRegistry.NewIntentListener, ActivityAware,EventChannel.StreamHandler {

    static String NOTIFICATION_DETAILS = "notificationDetails";
    private MethodChannel channel;
    private Context applicationContext;
    private Activity mainActivity;
    private static final String GET_NOTIFICATION_APP_LAUNCH_DETAILS_METHOD = "getNotificationAppLaunchDetails";
    private static final String SHOW_METHOD = "show";

    public static EventChannel.EventSink notificationClickedEvent;
    EventChannel _eventChannel;
    public static EventChannel.EventSink eventSink = null;
    private String initialMessage;
    private MethodChannel.Result showNotifResult;



    
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        onAttachedToFlutterEngine(flutterPluginBinding);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals(GET_NOTIFICATION_APP_LAUNCH_DETAILS_METHOD)) {
            Map<String,Object> data=new HashMap<>();
            boolean notifDismissed =MySharedPreferences.getBoolValue(applicationContext,Constants.IS_PUSH_NOTIFICATION_DISMISSED);
            data.put("payload",initialMessage);
            data.put("bNotifDismissed",notifDismissed);
            result.success(data);
            MySharedPreferences.setBoolValue(applicationContext,Constants.IS_PUSH_NOTIFICATION_DISMISSED,false);
            initialMessage=null;
        } else if (call.method.equals(SHOW_METHOD)) {
            if (applicationContext != null) {
                showNotifResult=result;
                show(call,applicationContext);
            } else {
                result.error("", "", "");
            }
        } else {
            result.notImplemented();
        }
    }


    private void onAttachedToFlutterEngine(FlutterPluginBinding flutterPluginBinding) {
        this.applicationContext = flutterPluginBinding.getApplicationContext();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "jg_local_notification");
        channel.setMethodCallHandler(this);
      _eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "jg_local_notification_event");
      _eventChannel.setStreamHandler(this);
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        this.mainActivity = null;
    }

    @Override
    public boolean onNewIntent(Intent intent) {
       Bundle extras = intent.getExtras();
       if (extras != null && extras.containsKey("LocalNotifClick") && extras.getBoolean("LocalNotifClick"))
       {
           if(extras.containsKey("payload")){
               String value = extras.getString("payload");
               Map<String,Object> data=new HashMap<>();
               data.put("payload",value);
               data.put("bNotifDismissed",false);
               if(eventSink!=null){
                   eventSink.success(data);
               }
           }
       }
        return false;
    }



    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {

        binding.addOnNewIntentListener(this);
        mainActivity = binding.getActivity();

      Bundle extras = mainActivity.getIntent().getExtras();
      if (extras != null && extras.containsKey("LocalNotifClick") && extras.getBoolean("LocalNotifClick"))
      {
        if(extras.containsKey("payload")){
          String value = extras.getString("payload");
          initialMessage=value;
        }
      }

    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }

    private void show(MethodCall call, Context context) {
        Map<String, Object> arguments = call.arguments();
        if (arguments != null) {
            NotificationEngine notificationEngine =new NotificationEngine();
            NotificationDataModel myNotificationDataModel=new NotificationDataModel(arguments);
            notificationEngine.show(myNotificationDataModel,context);
            showNotifResult.success(null);
        }else{
            showNotifResult.error("","Arguments are null","");
        }

    }

  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    this.eventSink = events;
  }

  @Override
  public void onCancel(Object arguments) {

  }

}
