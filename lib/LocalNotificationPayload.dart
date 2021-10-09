class LocalNotificationPayload {
  String title;
  String body;
  int notificationId;
  String icon;
  String? smallImage;
  String? largeImage;
  String? deepLinkURL;
  String? soundFileName;
  String? payload;
  String? channelId;
  String? channelName;
  LocalNotificationPayload(
      {required this.title,
      required this.body,
      required this.notificationId,
      required this.icon});
}
