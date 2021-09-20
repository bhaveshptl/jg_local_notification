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

  Future<String?> getNotificationAppLaunchDetails() async {
    String? result;
    try {
      result = await _channel.invokeMethod("getNotificationAppLaunchDetails");
      print(result);
    } catch (e) {
      print(e);
    }
    return result;
  }

  Stream<dynamic>? addNotificationClickEventListener() {
    if (clickEvent == null) {
      clickEvent = CLICKED_EVENT.receiveBroadcastStream();
    }

    return clickEvent;
  }
}
