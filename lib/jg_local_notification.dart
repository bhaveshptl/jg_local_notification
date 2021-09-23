import 'dart:async';

import 'package:flutter/services.dart';

class JgLocalNotification {
  static const MethodChannel _channel =
      const MethodChannel('jg_local_notification');

  static const EventChannel CLICKED_EVENT =
      const EventChannel("jg_local_notification_event");

  Stream<dynamic>? clickEvent;
  static Future<void> show(Map<String, dynamic> data) async {
    await _channel.invokeMethod('show', data);
  }

  Future<Map<String, dynamic>?> getNotificationAppLaunchDetails() async {
    Map<String, dynamic> data;
    try {
      data = (await _channel.invokeMethod("getNotificationAppLaunchDetails"))
          .cast<String, dynamic>();
    } catch (e) {
      return null;
    }
    return data;
  }

  Stream<dynamic>? addNotificationClickEventListener() {
    if (clickEvent == null) {
      clickEvent = CLICKED_EVENT.receiveBroadcastStream();
    }

    return clickEvent;
  }
}
