package com.guinseer.lifesaver.recievers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import com.guinseer.lifesaver.activities.LockScreenActivity;

import static android.content.Context.ACTIVITY_SERVICE;

public class LockscreenIntentReceiver extends BroadcastReceiver {

    // Handle actions and display Lockscreen
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (!isForeground("com.guinseer.lifesaver.activities.LockScreenActivity", context)) {

                start_lockscreen(context);

            }

        }

    }

    // Display lock screen
    private void start_lockscreen(Context context) {
        Log.d("notice ", "lockscreen");
        Intent mIntent = new Intent(context, LockScreenActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }

    public boolean isForeground(String myactivity, Context context) {

        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        Log.d("getclass", componentInfo.getClassName() + "");
        return componentInfo.getClassName().equals(myactivity);
    }

}
