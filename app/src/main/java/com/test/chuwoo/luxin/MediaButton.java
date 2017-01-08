package com.test.chuwoo.luxin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by zhuqunwu on 2016/5/19.
 */
public class MediaButton extends BroadcastReceiver {
    private static final BroadcastReceiver INSTANCE = new MediaButtonDisabler();
    　　@Override
    　　public void onReceive(Context context, Intent intent) {
        　　//Log.d(TAG, "Intercepted media button.");
        　　abortBroadcast();
        　　}
    　　public static void register(Context context) {
        　　IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        　　filter.setPriority(Integer.MAX_VALUE);
        　　context.registerReceiver(INSTANCE, filter);
        　　}
    　　public static void unregister(Context context) {
        　　context.unregisterReceiver(INSTANCE);
        　　}
}
