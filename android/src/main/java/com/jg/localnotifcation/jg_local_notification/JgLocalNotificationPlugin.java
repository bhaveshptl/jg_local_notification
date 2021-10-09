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
import io.flutter.plugin.common.PluginRegistry;
import android.os.Bundle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * LocalNotificationPlugin
 */
public class JgLocalNotificationPlugin implements FlutterPlugin, MethodCallHandler, PluginRegistry.NewIntentListener, ActivityAware, EventChannel.StreamHandler {

    private MethodChannel channel;
    private Context applicationContext;
    private Activity mainActivity;
    public static EventChannel.EventSink notificationClickedEvent;
    EventChannel _eventChannel;
    public static EventChannel.EventSink eventSink = null;
    private String initialMessage;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        onAttachedToFlutterEngine(flutterPluginBinding);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        onFlutterMethodCall(call,result);
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
        onNewIntentFired(extras);
        return false;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        binding.addOnNewIntentListener(this);
        mainActivity = binding.getActivity();
        Bundle extras = mainActivity.getIntent().getExtras();
        setLaunchData(extras);
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

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        this.eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {

    }

    private void showNotification(@NonNull MethodCall call, @NonNull Context context, @NonNull Result result) {
        Map<String, Object> arguments = call.arguments();
        if (arguments != null) {
            NotificationEngine notificationEngine = new NotificationEngine();
            NotificationDataModel myNotificationDataModel = new NotificationDataModel(arguments);
            notificationEngine.show(myNotificationDataModel, context);
            result.success(null);
        } else {
            result.error("", "Arguments are null", "");
        }
    }

    private void scheduleNotification(@NonNull MethodCall call, @NonNull Context context, @NonNull Result result) {
        if (call.arguments != null) {
            List arguments = call.arguments();
            Map<String, Object> data = (Map) arguments.get(0);
            int delayInMillis = (int) arguments.get(1);
            if (data != null) {
                NotificationEngine notificationEngine = new NotificationEngine();
                NotificationDataModel myNotificationDataModel = new NotificationDataModel(data);
                notificationEngine.scheduleNotification(myNotificationDataModel, context, delayInMillis);
                result.success(null);
            }
        } else {
            result.error("", "Arguments are null", "");
        }

    }

    private void onFlutterMethodCall(@NonNull MethodCall call, @NonNull Result result){
        if (call.method.equals("getNotificationAppLaunchDetails")) {
            if(applicationContext!=null){
                Map<String, Object> data = new HashMap<>();
                boolean isNotifDismissed = MySharedPreferences.getBoolValue(applicationContext, Constants.IS_PUSH_NOTIFICATION_DISMISSED);
                data.put("payload", initialMessage);
                data.put("bNotifDismissed", isNotifDismissed);
                result.success(data);
                MySharedPreferences.setBoolValue(applicationContext, Constants.IS_PUSH_NOTIFICATION_DISMISSED, false);
                initialMessage = null;
            }else{
                result.error("","","");
            }
        } else if (call.method.equals("show")) {
            if (applicationContext != null) {
                showNotification(call, applicationContext, result);
            } else {
                result.error("", "", "");
            }
        } else if (call.method.equals("scheduleNotification")) {
            if (applicationContext != null) {
                scheduleNotification(call, applicationContext, result);
            } else {
                result.error("", "", "");
            }
        } else {
            result.notImplemented();
        }
    }

    private void  onNewIntentFired(Bundle extras){
        if (extras != null && extras.containsKey("LocalNotifClick") && extras.getBoolean("LocalNotifClick")) {
            if (extras.containsKey("payload")) {
                String value = extras.getString("payload");
                Map<String, Object> data = new HashMap<>();
                data.put("payload", value);
                data.put("bNotifDismissed", false);
                if (eventSink != null) {
                    eventSink.success(data);
                }
            }
        }
    }

    private void  setLaunchData(Bundle extras){
        if (extras != null && extras.containsKey("LocalNotifClick") && extras.getBoolean("LocalNotifClick")) {
            if (extras.containsKey("payload")) {
                String value = extras.getString("payload");
                initialMessage = value;
            }
        }
    }

}
