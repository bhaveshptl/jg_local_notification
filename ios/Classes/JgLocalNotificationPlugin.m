#import "JgLocalNotificationPlugin.h"
#if __has_include(<jg_local_notification/jg_local_notification-Swift.h>)
#import <jg_local_notification/jg_local_notification-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "jg_local_notification-Swift.h"
#endif

@implementation JgLocalNotificationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftJgLocalNotificationPlugin registerWithRegistrar:registrar];
}
@end
