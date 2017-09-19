package com.supets.gaodeng.map;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * gaodeng
 *
 * @user lihongjiang
 * @description
 * @date 2017/9/18
 * @updatetime 2017/9/18
 */

public class MapAdapter extends BaseAdapter {

    public ArrayList<AddressInfo> data = new ArrayList<>();

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = new TextView(viewGroup.getContext());
        }
        TextView name = (TextView) view;
        name.setText(data.get(i).name+"\n"+data.get(i).address);
        return view;
    }

}
