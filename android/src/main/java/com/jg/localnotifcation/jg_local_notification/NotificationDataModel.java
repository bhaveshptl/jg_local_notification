package com.jg.localnotifcation.jg_local_notification;

import java.util.Map;
import java.util.Random;

public class NotificationDataModel {

    private String title;
    private String body;
    private String icon;
    private String smallImage;
    private String largeImage;
    private int notificationId;
    private String deepLinkURL;
    private String  channelId;
    private String channelName;
    private String soundFileName;
    private String payload;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String smallIcon) {
        this.icon = smallIcon;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }



    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {

        this.notificationId = notificationId;
    }

    public String getDeepLinkURL() {
        return deepLinkURL;
    }

    public void setDeepLinkURL(String deepLinkURL) {
        this.deepLinkURL = deepLinkURL;
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getSoundFileName() {
        return soundFileName;
    }

    public void setSoundFileName(String soundURI) {
        this.soundFileName = soundURI;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }

    public void  setNotificationData(Map<String, Object> arguments ){
        if(arguments.containsKey("title")){
            title=(String)arguments.get("title");
        }
        if(arguments.containsKey("body")){
            body=(String)arguments.get("body");
        }
        if(arguments.containsKey("icon")){
            icon =(String)arguments.get("icon");
        }
        if(arguments.containsKey("smallImage")){
            smallImage=(String)arguments.get("smallImage");
        }
        if(arguments.containsKey("id")){
            notificationId =(int) arguments.get("id");
        }else{
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(999);
            notificationId=randomInt;
        }
        if(arguments.containsKey("deepLinkURL")){
            deepLinkURL=(String)arguments.get("deepLinkURL");
        }
       if(arguments.containsKey("channelId")){
            channelId=(String)arguments.get("channelId");
        } if(arguments.containsKey("channelName")){
            channelName=(String)arguments.get("channelName");
        } if(arguments.containsKey("soundFileName")){
            soundFileName=(String)arguments.get("soundFileName");
        } if(arguments.containsKey("payload")){
            payload=(String)arguments.get("payload");
        }
        if(arguments.containsKey("largeImage")){
            largeImage=(String)arguments.get("largeImage");
        }
    }
}
