package com.guinseer.lifesaver.adapters;

/**
 * Created by chou6 on 2017-06-11.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import com.guinseer.lifesaver.R;
import com.guinseer.lifesaver.data.Schedule;


public class LAdapter extends BaseAdapter {

    ArrayList<Schedule> data = new ArrayList<Schedule>();
    Context context;

    ArrayList<View> viewlist = new ArrayList<View>();


    public LAdapter(Context context, ArrayList<Schedule> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {

        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.sche_list, null);
        }

        TextView t_list_name = (TextView) convertView.findViewById(R.id.t_list_name);
        TextView t_list_place = (TextView) convertView.findViewById(R.id.t_list_place);
        TextView t_list_time = (TextView) convertView.findViewById(R.id.t_list_time);
        TextView t_list_day = (TextView) convertView.findViewById(R.id.t_list_day);
        TextView t_list_apm = (TextView) convertView.findViewById(R.id.t_list_apm);
        CheckBox chk = (CheckBox) convertView.findViewById(R.id.ck_del);

        Schedule one = data.get(position);
        if (one.getChecked()) {
            chk.setVisibility(View.VISIBLE);
        } else {
            chk.setVisibility(View.INVISIBLE);
        }

        t_list_name.setText(one.getName());
        t_list_place.setText(one.getPlace());
        t_list_time.setText(String.format("%02d", one.getHour()) + ":" + String.format("%02d", one.getMin()));
        t_list_day.setText(one.getDay());
        t_list_apm.setText(one.getApm() + "  ");
        one.setV(convertView);
        return convertView;
    }
    /*
    Comparator<> nameAsc = new Comparator<order>() {
        @Override
        public int compare(order o1,order o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
    Comparator<> kindAsc = new Comparator<order>() {
        @Override
        public int compare(order o1, order o2) {
            return o1.getKind().compareTo(o2.getKind());
        }
    };




    public void setSort(int sortType){
        if(sortType == SORT_NAME){
            Collections.sort(data, nameAsc);
        }
        else{
            Collections.sort(data,kindAsc);
        }
        this.notifyDataSetChanged();
    }
    */

}

