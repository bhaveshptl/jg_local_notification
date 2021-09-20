package com.jg.localnotifcation.jg_local_notification;

import androidx.annotation.Keep;

@Keep
public class StringUtils {
    public static Boolean isNullOrEmpty(String string){
        return string == null || string.isEmpty();
    }
}
