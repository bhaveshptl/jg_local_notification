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
    private static final String INVALID_ICON_ERROR_CODE = "INVALID_ICON";
    private static final String DRAWABLE = "drawable";
    public static EventChannel.EventSink notificationClickedEvent;
    EventChannel _eventChannel;
    public static EventChannel.EventSink eventSink = null;
    private String initialMessage;
    private MethodChannel.Result showNotifResult;

    /*Notification Create*/
    NotificationManager myNotificationManager;
    NotificationCompat.Builder myNotificationBuilder;
    NotificationDataModel myNotificationDataModel;
    Intent myNotificationIntent;

    
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

    private boolean setNotificationData(Map<String, Object> arguments,Context context){
        myNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        myNotificationDataModel=new NotificationDataModel();
        myNotificationIntent = getLaunchIntent(context);
        myNotificationDataModel.setNotificationData(arguments);
        /*Set Intent Data*/
        myNotificationIntent.putExtra("notificationId", myNotificationDataModel.getNotificationId());
        myNotificationIntent.putExtra("source", "localNotif");
        myNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myNotificationIntent.putExtra("LocalNotifClick", true);
        if(myNotificationDataModel.getPayload()!=null){
            myNotificationIntent.putExtra("payload", myNotificationDataModel.getPayload());
        }
        myNotificationIntent.setAction(Long.toString(System.currentTimeMillis()));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, myNotificationIntent,
                PendingIntent.FLAG_ONE_SHOT);
        /*Set Notification Channel*/
        String channelId = "notifications";
        String channelName = "Push Notification Service";
        final int importance = NotificationManager.IMPORTANCE_HIGH;
        boolean enableVibration=true;

        Uri soundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (myNotificationDataModel.getSoundFileName()!=null&&!myNotificationDataModel.getSoundFileName().isEmpty()) {
            String soundfile = "";
            if (myNotificationDataModel.getSoundFileName().contains(".mp3") || myNotificationDataModel.getSoundFileName().contains(".ogg") || myNotificationDataModel.getSoundFileName().contains(".wav")) {
                soundfile = myNotificationDataModel.getSoundFileName().substring(0, (myNotificationDataModel.getSoundFileName().length() - 4));
            }
            if (!soundfile.isEmpty()) {
                soundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context
                        .getPackageName() + "/raw/" + soundfile);
            }
        }

        if(myNotificationDataModel.getChannelId()!=null && myNotificationDataModel.getChannelName()!=null){
            channelId=myNotificationDataModel.getChannelId();
            channelName=myNotificationDataModel.getChannelName();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId,channelName , importance);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setVibrationPattern(new long[]{1000, 1000});
            mChannel.enableVibration(enableVibration);
            mChannel.setSound(soundURI, audioAttributes);
            myNotificationManager.createNotificationChannel(mChannel);
        }

        /*Set Notification builder*/
        myNotificationBuilder = new NotificationCompat.Builder(context, channelId);

        if(myNotificationDataModel.getBody()!=null && myNotificationDataModel.getTitle()!=null){
            myNotificationBuilder.setContentTitle(myNotificationDataModel.getTitle());
            myNotificationBuilder.setContentText(myNotificationDataModel.getBody());
            myNotificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(myNotificationDataModel.getBody()));
        }else{
            return false;
        }
        myNotificationBuilder.setAutoCancel(true);
        myNotificationBuilder.setVibrate(new long[]{1000, 1000});
        myNotificationBuilder.setSound(soundURI);
        myNotificationBuilder.setDeleteIntent(createOnDismissedIntent(context.getApplicationContext(),myNotificationDataModel.getPayload()));
        myNotificationBuilder.setContentIntent(pendingIntent);
        boolean hasAInvalidIcon=hasAInvalidIcon(myNotificationDataModel.getIcon());
        if(hasAInvalidIcon){
            return false;
        }else{
            myNotificationBuilder.setSmallIcon(getDrawableResourceId(context, myNotificationDataModel.getIcon()));
            myNotificationBuilder.setColor(Color.parseColor("#d32518"));
        }
        return true;
    }


    private void show(MethodCall call, Context context) {
        Map<String, Object> arguments = call.arguments();
        if (arguments != null) {
            boolean isValidData=setNotificationData(arguments,context);
            if(isValidData && myNotificationManager!=null && myNotificationBuilder!=null && myNotificationDataModel!=null) {

                if(myNotificationDataModel.getLargeImage()!=null&&!myNotificationDataModel.getLargeImage().isEmpty()){
                    DownloadImageAsync asyncTask = (DownloadImageAsync) new DownloadImageAsync(myNotificationDataModel.getLargeImage(),new DownloadImageAsync.DownloadImageAsyncResponse(){
                        @Override
                        public void onImageDownloaded(Bitmap output){
                            if(output!=null){
                                myNotificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(output).setBigContentTitle(myNotificationDataModel.getTitle()).setSummaryText(myNotificationDataModel.getBody()));

                                myNotificationManager.notify(myNotificationDataModel.getNotificationId(), myNotificationBuilder.build());
                            }
                            showNotifResult.success(null);
                        }
                    }).execute();
                }
                else if(myNotificationDataModel.getSmallImage()!=null&& !myNotificationDataModel.getSmallImage().isEmpty()){
                    DownloadImageAsync asyncTask = (DownloadImageAsync) new DownloadImageAsync(myNotificationDataModel.getSmallImage(),new DownloadImageAsync.DownloadImageAsyncResponse(){
                        @Override
                        public void onImageDownloaded(Bitmap output){
                            if(output!=null){
                                myNotificationBuilder.setLargeIcon(output);
                                myNotificationManager.notify(myNotificationDataModel.getNotificationId(), myNotificationBuilder.build());
                            }
                            showNotifResult.success(null);
                        }
                    }).execute();
                }else{
                    myNotificationManager.notify(myNotificationDataModel.getNotificationId(), myNotificationBuilder.build());
                    showNotifResult.success(null);
                }

            }else{
                showNotifResult.error("","","");
            }
        }else{
            showNotifResult.error("","Arguments are null","");
        }
    }

    private PendingIntent createOnDismissedIntent(Context context,String payload) {
        Intent intent = new Intent(context, MyNotificationDismissedReceiver.class);
        intent.putExtra("payload", payload);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private boolean hasAInvalidIcon(String icon) {
        return !StringUtils.isNullOrEmpty(icon) && !isValidDrawableResource(applicationContext, icon,INVALID_ICON_ERROR_CODE);
    }


    private static boolean isValidDrawableResource(Context context, String name,  String errorCode) {
        int resourceId = context.getResources().getIdentifier(name, DRAWABLE, context.getPackageName());
        if (resourceId == 0) {
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
