package com.guinseer.lifesaver.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.guinseer.lifesaver.utils.LockscreenUtils;
import com.guinseer.lifesaver.R;
import com.guinseer.lifesaver.communications.RSS;

/**
 * @desc this class will hold functions for user interaction
 * examples include lock_init(), user_username(), user_age(), user_regdate()
 * @author Even Seung-Eon Choi choi610@gmail.com
 * @required LockscreenUtils.java
 */
public class LockScreenActivity extends Activity implements
        LockscreenUtils.OnLockStatusChangedListener {

    // begin variable listing
    private Button btnUnlock;
    FrameLayout overall;
    private LockscreenUtils mLockscreenUtils;
    int MillisecondTime, StartTime, TimeBuff, UpdateTime = 0;
    SQLiteDatabase database;
    Thread main;
    Handler handler;
    int start_hour, start_miniute;
    int Hours, Seconds, Minutes;
    TextView clock, t_info;
    Calendar c;
    String[] date = {"일", "월", "화", "수", "목", "금", "토"};
    String curday;
    int index;
    int[] data;
    boolean show_flag = true;
    boolean runmain = true;
    ImageView img_weather, img_cloth;

    /**
     * @desc main routine starts from here
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);
        lock_init();

    }

    /**
     * @desc initalize all varibles listed above
     */
    private void lock_init() {
        // get system clock
        c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        //set event listener for mLockscreenUtils (see OnLockStatusChanged in this class for more details)
        /** commented this code since locking home button causesd many inconveniences for user environment.
            * mLockscreenUtils = new LockscreenUtils();
            * mLockscreenUtils.setOnCustomAddListener(this);
         */
        //Layout that handles touch event for lockscreen
        overall = (FrameLayout) findViewById(R.id.frameLayout);
        overall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("unlock", "unlock");
                unlockDevice();
            }
        });

        clock = (TextView) findViewById(R.id.clock); //Textview used to display clock

        t_info = (TextView) findViewById(R.id.t_info); //Textview used to show schedule information

        img_weather = (ImageView) findViewById(R.id.img_umbre); //Imageview for showing weather-related images
        img_cloth = (ImageView) findViewById(R.id.img_wear); //Imageview for showing weather-related images
        //Handler for modifying views(Updating  weather or cloth Images , time left etc..) on UI thread
        handler = new Handler() {
            public void handleMessage(Message msg) {
                String message = (String) msg.obj; //Extract the string from the Message
                //Extract the string from the Message
                if (message == null) {
                    switch (msg.what) {
                        case 0:
                            img_weather.setImageResource(R.drawable.umbrella);
                            break;
                        case 1:
                            img_weather.setImageResource(R.drawable.snow);
                        case 2:
                            img_cloth.setImageResource(R.drawable.tshirt);
                            break;
                        case 3:
                            img_cloth.setImageResource(R.drawable.pullover);
                            break;
                        case 4:
                            img_cloth.setImageResource(R.drawable.jacket);
                            break;
                        case 5:
                            img_cloth.setImageResource(0);
                            break;
                        case 6:
                            clock.setVisibility(View.VISIBLE);
                            clock.setText("-" + Hours + ":" + Minutes + ":"
                                    + String.format("%02d", Seconds));
                            break;
                        case 7:
                            clock.setVisibility(View.GONE);
                            t_info.setText("표시할 스케줄이 없습니다.");
                            break;
                    }
                } else {
                    Log.d("message", message);
                    t_info.setText(message);
                }


            }
        };
        index = c.get(Calendar.DAY_OF_WEEK) - 1;
        Log.d("now", index + "");
        curday = date[index];
        this.getWindow().setType(
                LayoutParams.TYPE_SYSTEM_ALERT);
        this.getWindow().addFlags(
                LayoutParams.FLAG_FULLSCREEN
                        | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | LayoutParams.FLAG_KEEP_SCREEN_ON
                        | LayoutParams.FLAG_DISMISS_KEYGUARD
        );


        // unlock screen in case of app get killed by system
        if (getIntent() != null && getIntent().hasExtra("kill")
                && getIntent().getExtras().getInt("kill") == 1) {
            Log.d("warning", "system unlock");
            enableKeyguard();
            unlockDevice();;
        } else {
            try {
                // disable default lock screen
                disableKeyguard();
                // was on the open source for blocking home and tested with Junit but may not work on latest versions.
                //lockHomeButton();
                // listen to the events get fired during the call
                StateListener phoneStateListener = new StateListener();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telephonyManager.listen(phoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //Handler for modifying views(Updating  weather or cloth Images , time left etc..) on UI thread
        main = new Thread() {
            @Override
            public void run() {
                while (runmain) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    c = Calendar.getInstance();
                    int curtime = c.get(Calendar.SECOND) + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.HOUR_OF_DAY) * 60 * 60;

                    int diff = StartTime - curtime;
                    if (diff < 0)
                        diff = (24 * 60 * 60) + StartTime - curtime;
                    Seconds = diff;
                    Minutes = Seconds / 60;
                    Hours = Minutes / 60;
                    Minutes = Minutes % 60;
                    Seconds = Seconds % 60;
                    if ((c.get(Calendar.HOUR_OF_DAY)) == 0) {
                        getLatestWeather();
                    } else if (Seconds % 10 == 0) {
                        getLatestWeather();
                    }

                    if (Seconds % 10 == 0) {
                        data = getLatestSchedule(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                        if (data[0] != -1) {
                            show_flag = true;
                            StartTime = (data[0] * 3600 + data[1] * 60);
                            Log.d("StartTime : ", data[0] + " " + data[1]);
                        } else {
                            show_flag = false;
                        }
                    }

                    if (show_flag) {
                        handler.sendEmptyMessage(6);

                    } else {
                        handler.sendEmptyMessage(7);

                    }
                }


            }

        };
        main.start();
        start_hour = -1;
        start_miniute = -1;
        database = openOrCreateDatabase("choi", MODE_PRIVATE, null);
        data = getLatestSchedule(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        if (data[0] != -1)
            StartTime = (data[0] * 3600 + data[1] * 60);
        else {
            show_flag = false;

        }
        img_cloth.setImageResource(0);
        img_weather.setImageResource(0);
        //handler.postDelayed(timer, 0);

    }

    // Handle events of calls and unlock screen if necessary
    private class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    unlockDevice();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    }

    // Don't finish Activity on Back press
    @Override
    public void onBackPressed() {
        return;
    }

    // Handle button clicks
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (keyCode == KeyEvent.KEYCODE_POWER)
                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
            return true;
        }
        return (keyCode == KeyEvent.KEYCODE_HOME);

    }

    // handle the key press events here itself
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return false;
        }
        return (event.getKeyCode() == KeyEvent.KEYCODE_HOME);
    }

    // Lock home button
    public void lockHomeButton() {
        mLockscreenUtils.lock(LockScreenActivity.this);
    }

    // Unlock home button and wait for its callback
    public void unlockHomeButton() {
        mLockscreenUtils.unlock();
    }

    // Simply unlock device when home button is successfully unlocked
    @Override
    public void onLockStatusChanged(boolean isLocked) {
        if (!isLocked) {
            unlockDevice();
        }
    }

    public int[] getLatestSchedule(int input_hour, int input_min) {
        int[] temp = new int[2];

        temp[0] = -1;
        temp[1] = -1;
        // ArrayList<int[]> cand = new ArrayList<>();
        Cursor info = getsql("select hour, min, apm, name, place from Schedule where day = '" + curday + "' order by hour asc, min asc;");
        info.moveToFirst();
        if (info != null && info.getCount() > 0) {
            do {
                int hour = Integer.parseInt(info.getString(0));
                int min = Integer.parseInt(info.getString(1));
                Log.d("curday0", "detected " + input_hour + " " + input_min + " " + hour + " " + min + " " + curday);
                String apm = info.getString(2);
                if (apm.equals("오후")) {
                    hour = hour + 12;
                }
                if ((input_hour < hour) || (input_hour == hour && input_min < min)) {
                    temp[0] = hour;
                    temp[1] = min;
                    String message = info.getString(3) + " - " + info.getString(4);
                    Message msg = Message.obtain(); // Creates an new Message instance
                    msg.obj = message; // Put the string into Message, into "obj" field.
                    msg.setTarget(handler); // Set the Handler
                    msg.sendToTarget(); //Send the message
                    Log.d("curday", "detected " + input_hour + " " + input_min + " " + temp[0] + " " + temp[1]);
                    break;
                }
            } while (info.moveToNext());
        } else {
            info = getsql("select hour, min, apm, name, place from Schedule where day = '" + date[(index + 1) % 7] + "' order by hour asc, min asc;");
            info.moveToFirst();
            if (info != null && info.getCount() > 0) {
                int hour = Integer.parseInt(info.getString(0));
                int min = Integer.parseInt(info.getString(1));
                String apm = info.getString(2);
                String message = info.getString(3) + " - " + info.getString(4);
                Message msg = Message.obtain(); // Creates an new Message instance
                msg.obj = message; // Put the string into Message, into "obj" field.
                msg.setTarget(handler); // Set the Handler
                msg.sendToTarget(); //Send the message
                if (apm.equals("오후")) {
                    hour = hour + 12;

                }
                temp[0] = hour + 24;
                temp[1] = min + 24;
                Log.d("curday_1", "detected " + input_hour + " " + input_min + " " + temp[0] + " " + temp[1]);
            }

        }
        info.close();
        return temp;
    }

    public void getLatestWeather() {
        new Thread() {
            public void run() {
                RSS rss = new RSS(getApplicationContext());
                List<Map<String, String>> weather = rss.find();
                for (Map<String, String> tmp : weather) {
                    String s = tmp.get("날씨");
                    if (s.equals("비") || s.equals("눈/비") || s.equals("비/눈")) {
                        handler.sendEmptyMessage(0);
                        break;
                    } else if (s.equals("눈")) {
                        handler.sendEmptyMessage(1);
                        break;
                    }
                }
                double low = Double.parseDouble(weather.get(0).get("최저"));
                double high = Double.parseDouble(weather.get(0).get("최고"));
                if (high - low < 20) {
                    if (high >= 20) {
                        handler.sendEmptyMessage(2);

                    } else if (low > 10 && high < 20) {
                        handler.sendEmptyMessage(3);

                    } else if (high < 10) {
                        handler.sendEmptyMessage(4);

                    }
                } else {
                    handler.sendEmptyMessage(5);
                }
            }
        }.start();
    }

    @SuppressWarnings("deprecation")
    private void disableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.disableKeyguard();
    }

    @SuppressWarnings("deprecation")
    private void enableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.reenableKeyguard();
    }

    //Simply unlock device by finishing the activity
    private void unlockDevice() {
        Log.d("info", "finished");

        runmain = false;
        finish();
    }

    private boolean isServiceRunning(String serviceName) {
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i
                    .next();

            if (runningServiceInfo.service.getClassName().equals(serviceName)) {
                serviceRunning = true;

                if (runningServiceInfo.foreground) {
                    //service run in foreground
                }
            }
        }
        return serviceRunning;
    }

    public Cursor getsql(String s) {
        Cursor temp = null;
        try {
            temp = database.rawQuery(s, null);
            temp.moveToFirst();

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return temp;
    }

}