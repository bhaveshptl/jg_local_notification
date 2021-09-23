package com.jg.localnotifcation.jg_local_notification;

import android.app.Activity;
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
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.PluginRegistry;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.text.Spanned;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.app.AlarmManagerCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;


import java.io.FileInputStream;
import java.io.IOException;

import java.util.HashMap;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.view.FlutterMain;

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
    private static final String INVALID_ICON_ERROR_CODE = "INVALID_ICON";
    private static final String DRAWABLE = "drawable";
    private static final String SELECT_NOTIFICATION = "SELECT_NOTIFICATION";
    private static final String INVALID_DRAWABLE_RESOURCE_ERROR_MESSAGE = "The resource %s could not be found. Please make sure it has been added as a drawable resource to your Android head project.";
    private static final String PAYLOAD = "payload";

    public static EventChannel.EventSink notificationClickedEvent;
    private MethodChannel _dataChannel;
    EventChannel _eventChannel;
    public static EventChannel.EventSink eventSink = null;
    private String initialMessage;


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
                show(call, result, applicationContext);
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

    private void show(MethodCall call, MethodChannel.Result result, Context context) {
        Map<String, Object> arguments = call.arguments();
        if (arguments != null&&arguments.containsKey("id")&&arguments.containsKey("title")&&arguments.containsKey("body")&&arguments.containsKey("icon")) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent resultIntent = getLaunchIntent(context);
            NotificationCompat.Builder notificationBuilder;

            String payload = (String) arguments.get("payload");
            Integer id = (Integer) arguments.get("id");
            String title = (String) arguments.get("title");
            String body = (String) arguments.get("body");
            String icon = (String) arguments.get("icon");

            resultIntent.putExtra("notificationId", id);
            resultIntent.putExtra("source", "localNotif");
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra("LocalNotifClick", true);
            resultIntent.putExtra("payload", payload);
            resultIntent.setAction(Long.toString(System.currentTimeMillis()));


            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String channelId = "notifications";
            String channelName = "Push Notification Service";
            final int importance = NotificationManager.IMPORTANCE_HIGH;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                mChannel.setVibrationPattern(new long[]{1000, 1000});
                mChannel.enableVibration(true);
                mChannel.setSound(defaultSoundUri, audioAttributes);
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationBuilder = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{1000, 1000})
                    .setSound(defaultSoundUri)
                    .setDeleteIntent(createOnDismissedIntent(context.getApplicationContext()))
                    .setContentIntent(pendingIntent);

            boolean validIcon=hasInvalidIcon(result, icon);
            if(!validIcon){
                notificationBuilder.setSmallIcon(getDrawableResourceId(context, icon));
                notificationBuilder.setColor(Color.parseColor("#d32518"));
                notificationManager.notify(id, notificationBuilder.build());
            }
            result.success(null);
        }else{
            result.success(null);
        }
    }

    private PendingIntent createOnDismissedIntent(Context context) {
        Intent intent = new Intent(context, MyNotificationDismissedReceiver.class);
        intent.putExtra("notificationId", "");
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private boolean hasInvalidIcon(MethodChannel.Result result, String icon) {
        return !StringUtils.isNullOrEmpty(icon) && !isValidDrawableResource(applicationContext, icon, result, INVALID_ICON_ERROR_CODE);
    }

    private static boolean isValidDrawableResource(Context context, String name, MethodChannel.Result result, String errorCode) {
        int resourceId = context.getResources().getIdentifier(name, DRAWABLE, context.getPackageName());
        if (resourceId == 0) {
            result.error(errorCode, String.format(INVALID_DRAWABLE_RESOURCE_ERROR_MESSAGE, name), null);
            return false;
        }
        return true;
    }


    private static Intent getLaunchIntent(Context context) {
        String packageName = context.getPackageName();
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getLaunchIntentForPackage(packageName);
    }

    private static int getDrawableResourceId(Context context, String name) {
        return context.getResources().getIdentifier(name, DRAWABLE, context.getPackageName());
    }

  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    this.eventSink = events;
  }

  @Override
  public void onCancel(Object arguments) {

  }
}
