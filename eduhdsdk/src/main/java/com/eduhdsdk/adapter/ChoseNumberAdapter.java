package com.eduhdsdk.adapter;

import android.content.Context;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eduhdsdk.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/6/28/028.
 */

public class ChoseNumberAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> data;

    public ChoseNumberAdapter(Context context, List<String> data) {
        this.mContext = context;
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
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChoseNumberAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new ChoseNumberAdapter.ViewHolder();
            LayoutInflater _LayoutInflater = LayoutInflater.from(mContext);
            convertView = _LayoutInflater.inflate(R.layout.layout_page_number_choose_item, null);
            holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            convertView.setTag(holder);
        } else {
            holder = (ChoseNumberAdapter.ViewHolder) convertView.getTag();
        }
        holder.tv_number.setText(data.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView tv_number;
    }
}
