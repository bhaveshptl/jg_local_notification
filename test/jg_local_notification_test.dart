import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:jg_local_notification/jg_local_notification.dart';

void main() {
  const MethodChannel channel = MethodChannel('jg_local_notification');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await JgLocalNotification.show, '42');
  });
}
