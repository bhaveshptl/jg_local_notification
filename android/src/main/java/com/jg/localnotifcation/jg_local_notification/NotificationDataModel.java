package com.jg.localnotifcation.jg_local_notification;

import java.io.Serializable;
import java.util.Map;
import java.util.Random;

public class NotificationDataModel   implements Serializable {

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

    NotificationDataModel(Map<String, Object> arguments){
        setNotificationData(arguments);
    }


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

    private void  setNotificationData(Map<String, Object> arguments ){
        if(arguments.containsKey("title")){
            this.title=(String)arguments.get("title");
        }
        if(arguments.containsKey("body")){
            this.body=(String)arguments.get("body");
        }
        if(arguments.containsKey("icon")){
            this.icon =(String)arguments.get("icon");
        }
        if(arguments.containsKey("smallImage")){
            this.smallImage=(String)arguments.get("smallImage");
        }
        if(arguments.containsKey("id")){
            this.notificationId =(int) arguments.get("id");
        }else{
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(999);
            this.notificationId=randomInt;
        }
        if(arguments.containsKey("deepLinkURL")){
            this.deepLinkURL=(String)arguments.get("deepLinkURL");
        }
       if(arguments.containsKey("channelId")){
            this.channelId=(String)arguments.get("channelId");
        } if(arguments.containsKey("channelName")){
            this.channelName=(String)arguments.get("channelName");
        } if(arguments.containsKey("soundFileName")){
            this.soundFileName=(String)arguments.get("soundFileName");
        } if(arguments.containsKey("payload")){
            this.payload=(String)arguments.get("payload");
        }
        if(arguments.containsKey("largeImage")){
            this.largeImage=(String)arguments.get("largeImage");
        }
    }
}
