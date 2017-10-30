package com.guinseer.lifesaver.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.guinseer.lifesaver.R;
import com.guinseer.lifesaver.data.Schedule;
import com.guinseer.lifesaver.adapters.LAdapter;
import com.guinseer.lifesaver.services.LockscreenService;

public class Intro extends AppCompatActivity implements TextView.OnEditorActionListener {
    ListView listView;
    ArrayList<Schedule> data, origin;
    LAdapter adapter;
    CheckBox delcheck;
    /////////////
    LinearLayout list, reg;
    LinearLayout lastevent, lastboard;
    LinearLayout sec_day, sec_time;
    LinearLayout hourboard_apm, hourboard_hour, hourboard_min;
    /////////////
    LinearLayout show_delete;
    Button b_new_sche;
    ////////////
    TextView t_apm, t_hr, t_min, t_day;
    Toolbar toolbar;
    EditText e_name, e_location;
    String cur = "list";
    String mode = "normal";
    boolean modify_flag = false;
    View target;
    SQLiteDatabase database;
    Schedule cur_schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        init();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_sche:
                switch_layout();
                if (!modify_flag) {
                    e_name.setText("");
                    e_location.setText("");
                    t_day.setText("");
                    t_hr.setText("");
                    t_min.setText("");
                }
                break;
            case R.id.b_cancel:
                switch_layout();
                refresh();
                break;
            case R.id.b_submit:
                register();

                break;
            case R.id.b_del_cancel:
                show_delist(false);
                change_mode_layout();
                break;
            case R.id.b_del_confirm:
                del_list();
                break;
        }

    }

    public void onTabPressed(View v) {
        switch (v.getId()) {
            case R.id.b_mon:
                search("월");
                break;
            case R.id.b_tue:
                search("화");
                break;
            case R.id.b_wed:
                search("수");
                break;
            case R.id.b_tur:
                search("목");
                break;
            case R.id.b_fri:
                search("금");
                break;
            case R.id.b_sat:
                search("토");
                break;
            case R.id.b_sun:
                search("일");
                break;
        }
    }

    public void onViewPressed(View v) {
        switch (v.getId()) {
            case R.id.e_name:
                reset("lastevent");
                reset("lastboard");
                break;
            case R.id.e_location:

                reset("lastevent");
                reset("lastboard");
                break;
            case R.id.t_day:
                pop("lastevent", sec_day);
                reset("lastboard");

                break;
            case R.id.t_apm:
                pop("lastevent", sec_time);
                pop("lastboard", hourboard_apm);

                break;
            case R.id.t_hr:
                pop("lastevent", sec_time);
                pop("lastboard", hourboard_hour);
                break;
            case R.id.t_min:
                pop("lastevent", sec_time);
                pop("lastboard", hourboard_min);
                break;
        }
    }

    public void onkeyPressed(View v) {
        switch (v.getId()) {
            case R.id.b_am:
                t_apm.setText("오전");
                pop("lastevent", sec_time);
                pop("lastboard", hourboard_hour);
                break;
            case R.id.b_pm:
                t_apm.setText("오후");
                pop("lastevent", sec_time);
                pop("lastboard", hourboard_hour);
                break;
            case R.id.b_dzero:
                t_min.setText("00");
                break;
            case R.id.b_dhalf:
                t_min.setText("30");
                break;
            case R.id.b_one:
                checkinput("1");
                break;
            case R.id.b_two:
                checkinput("2");
                break;
            case R.id.b_three:
                checkinput("3");
                break;
            case R.id.b_four:
                checkinput("4");
                break;
            case R.id.b_five:
                checkinput("5");
                break;
            case R.id.b_six:
                checkinput("6");
                break;
            case R.id.b_seven:
                checkinput("7");
                break;
            case R.id.b_eight:
                checkinput("8");
                break;
            case R.id.b_nine:
                checkinput("9");
                break;
            case R.id.b_zero:
                checkinput("0");
                break;
            case R.id.b_pass:
                pop("lastevent", sec_time);
                pop("lastboard", hourboard_min);
                break;
        }
    }

    public void onImagePressed(View v) {
        switch (v.getId()) {
            case R.id.mon:
                finish_day("월");
                break;
            case R.id.tue:
                finish_day("화");
                break;
            case R.id.wed:
                finish_day("수");
                break;
            case R.id.tur:
                finish_day("목");
                break;
            case R.id.fri:
                finish_day("금");
                break;
            case R.id.sat:
                finish_day("토");
                break;
            case R.id.sun:
                finish_day("일");
                break;
        }
    }

    public void init() {
        list = (LinearLayout) findViewById(R.id.show);
        reg = (LinearLayout) findViewById(R.id.select);
        sec_day = (LinearLayout) findViewById(R.id.sec_day);
        sec_time = (LinearLayout) findViewById(R.id.sec_time);
        show_delete = (LinearLayout) findViewById(R.id.show_delete);
        b_new_sche = (Button) findViewById(R.id.new_sche);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("일정관리");
        toolbar.setNavigationIcon(R.drawable.plant);
        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#894e3a")));
        toolbar.setTitleTextColor(Color.WHITE);
        e_name = (EditText) findViewById(R.id.e_name);

        e_location = (EditText) findViewById(R.id.e_location);
        e_location.setOnEditorActionListener(this);
        hourboard_apm = (LinearLayout) findViewById(R.id.hourboard_apm);
        hourboard_hour = (LinearLayout) findViewById(R.id.hourboard_digit_hour);
        hourboard_min = (LinearLayout) findViewById(R.id.hourboard_min);
        t_day = (TextView) findViewById(R.id.t_day);
        t_apm = (TextView) findViewById(R.id.t_apm);
        t_hr = (TextView) findViewById(R.id.t_hr);
        t_min = (TextView) findViewById(R.id.t_min);
        listView = (ListView) findViewById(R.id.list);
        data = new ArrayList<Schedule>();
        origin = new ArrayList<Schedule>();
        adapter = new LAdapter(this, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mode.equals("normal")) {
                    switch_layout();
                    Schedule temp = data.get(position);
                    e_name.setText(temp.getName());
                    e_location.setText(temp.getPlace());
                    t_apm.setText(temp.getApm());
                    t_day.setText(temp.getDay());
                    t_hr.setText(temp.getHour() + "");
                    t_min.setText(temp.getMin() + "");
                    cur_schedule = temp;
                    modify_flag = true;
                } else {
                    return;
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mode.equals("normal")) {
                    change_mode_layout();
                    show_delist(true);
                } else {

                }
                return true;
            }
        });
        database = openOrCreateDatabase("choi", MODE_PRIVATE, null);

        dosql("create table if not exists Schedule(" +
                "name char(100) not null," +
                "place char(100) not null," +
                "day char(20) not null," +
                "apm char(20) not null," +
                "hour integer not null," +
                "min integer not null" +
                ");");
        Cursor temp = getsql("select * from Schedule;");
        temp.moveToFirst();
        if (temp != null && temp.getCount() > 0) {

            do {
                origin.add(new Schedule(temp.getString(0), temp.getString(1), temp.getString(2),
                        temp.getString(3), Integer.parseInt(temp.getString(4)), Integer.parseInt(temp.getString(5))));
            } while (temp.moveToNext());
        } else {
            Log.d("info", "data not foud");
        }

        startService(new Intent(this, LockscreenService.class));


        refresh();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                sec_day.setVisibility(View.VISIBLE);
                lastevent = sec_day;
                break;
        }
        return false;
    }

    public void reset(String temp) {
        if (temp.equals("lastevent") && lastevent != null) {
            lastevent.setVisibility(View.GONE);
            lastevent = null;
        } else if (temp.equals("lastboard") && lastboard != null) {
            lastboard.setVisibility(View.GONE);
            lastboard = null;
        }
    }

    public void pop(String temp, LinearLayout target) {

        if (temp.equals("lastevent")) {
            reset(temp);
            lastevent = target;
            lastevent.setVisibility(View.VISIBLE);

        } else if (temp.equals("lastboard")) {
            reset(temp);
            lastboard = target;
            lastboard.setVisibility(View.VISIBLE);

        }

    }

    public void checkinput(String s) {
        t_hr.append(s);
        if (t_hr.length() == 2) {
            int num = Integer.parseInt(t_hr.getText().toString());
            if (num > 12 || num == 0) {
                t_hr.setText("");
                return;
            }
            pop("lastevent", sec_time);
            pop("lastboard", hourboard_min);

        } else if (t_hr.length() == 3) {
            t_hr.setText("");
            return;
        }

    }

    public void finish_day(String s) {
        t_day.setText(s);
        pop("lastevent", sec_time);
        pop("lastboard", hourboard_apm);
    }

    public void register() {
        if (e_name.length() == 0) {
            Toast.makeText(this, "일정명을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (e_location.length() == 0) {
            Toast.makeText(this, "장소명을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (t_day.length() == 0) {
            Toast.makeText(this, "요일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (t_apm.length() == 0) {
            Toast.makeText(this, "오전/오후를 체크해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (t_hr.length() == 0) {
            Toast.makeText(this, "시간을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (t_min.length() == 0) {
            Toast.makeText(this, "분을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (modify_flag) {

            Cursor check = getsql("select * from Schedule where hour = " +
                    Integer.parseInt(t_hr.getText().toString()) + " and min = " + t_min.getText().toString() +
                    " and apm = '" + t_apm.getText().toString() + "' and day = '" + t_day.getText().toString() + "'");
            check.moveToFirst();
            if (check.getCount() > 0) {
                Toast.makeText(this, "동일한 시간대가 존재합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            dosql("update Schedule set name = '" + e_name.getText() +
                    "', place = '" + e_location.getText().toString() +
                    "', day = '" + t_day.getText().toString() +
                    "', apm = '" + t_apm.getText().toString() +
                    "', hour = " + t_hr.getText().toString() +
                    ", min = " + t_min.getText().toString() +
                    " where hour = " + cur_schedule.getHour() + " and min = " + cur_schedule.getMin() +
                    " and day = '" + cur_schedule.getDay() + "' and apm = '" + cur_schedule.getApm() + "';");
            cur_schedule.setTable(e_name.getText().toString(), e_location.getText().toString(),
                    t_day.getText().toString(), t_apm.getText().toString(),
                    Integer.parseInt(t_hr.getText().toString()), Integer.parseInt(t_min.getText().toString()));

            cur_schedule = null;
            modify_flag = false;
        } else {
            Cursor check = getsql("select * from Schedule where hour = " +
                    Integer.parseInt(t_hr.getText().toString()) + " and min = " + t_min.getText().toString() +
                    " and apm = '" + t_apm.getText().toString() + "' and day = '" + t_day.getText().toString() + "'");
            check.moveToFirst();
            if (check.getCount() > 0) {
                Toast.makeText(this, "동일한 시간대가 존재합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            Schedule temp = new Schedule(e_name.getText().toString(), e_location.getText().toString(),
                    t_day.getText().toString(), t_apm.getText().toString(),
                    Integer.parseInt(t_hr.getText().toString()), Integer.parseInt(t_min.getText().toString()));
            origin.add(temp);
            dosql("insert into Schedule values('" + e_name.getText().toString() + "','" + e_location.getText().toString() + "','" +
                    t_day.getText().toString() + "','" + t_apm.getText().toString() + "',"
                    + Integer.parseInt(t_hr.getText().toString()) + ",'" + Integer.parseInt(t_min.getText().toString()) + "');");

        }

        refresh();
        switch_layout();

    }

    public void switch_layout() {
        if (cur.equals("list")) {
            list.setVisibility(View.GONE);
            reg.setVisibility(View.VISIBLE);
            cur = "register";
            e_name.requestFocus();
        } else {
            list.setVisibility(View.VISIBLE);
            reg.setVisibility(View.GONE);
            cur = "list";
        }

    }

    public void search(String s) {
        data.clear();
        for (Schedule t : origin) {
            if (t.getDay().equals(s)) {
                data.add(t);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void refresh() {
        modify_flag = false;
        data.clear();
        data.addAll(origin);
        adapter.notifyDataSetChanged();
    }

    public void show_delist(boolean in) {
        if (in) {
            for (int i = 0; i < data.size(); i++) {
                if (!data.get(i).getChecked())
                    data.get(i).setChecked(in);
            }
        } else {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getChecked())
                    data.get(i).setChecked(in);
            }
        }

        adapter.notifyDataSetChanged();
    }

    public void del_list() {
        Schedule t = null;
        for (int i = 0; i < data.size(); i++) {

            target = data.get(i).getV();
            delcheck = (CheckBox) target.findViewById(R.id.ck_del);
            data.get(i).setChecked(false);
            if (delcheck.isChecked()) {
                t = data.get(i);
                data.remove(i);
                i--;
            }
            for (int j = 0; j < origin.size(); j++) {
                if (origin.get(j) == t) {
                    origin.remove(j);
                    dosql("delete from Schedule where hour = " + t.getHour() + " and min = " + t.getMin() +
                            " and day = '" + t.getDay() + "' and apm = '" + t.getApm() + "';");
                }
            }
        }
        adapter.notifyDataSetChanged();
        change_mode_layout();
    }

    public void change_mode_layout() {
        if (mode.equals("normal")) {
            mode = "del_mode";
            b_new_sche.setVisibility(View.GONE);
            show_delete.setVisibility(View.VISIBLE);
        } else {
            mode = "normal";
            b_new_sche.setVisibility(View.VISIBLE);
            show_delete.setVisibility(View.GONE);
        }
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

    public void dosql(String s) {
        database.execSQL(s);
    }

    public String getExternalPath() {
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {

            sdPath =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
            Log.d("log", sdPath);
            //sdPath = "/mnt/sdcard/";
        } else
            sdPath = getFilesDir() + "";

        return sdPath;
    }

}
