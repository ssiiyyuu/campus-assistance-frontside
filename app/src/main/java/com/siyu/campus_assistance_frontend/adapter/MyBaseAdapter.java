package com.siyu.campus_assistance_frontend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.siyu.campus_assistance_frontend.R;

import java.util.List;

import lombok.Data;

public class MyBaseAdapter extends BaseAdapter {
    private final Context context;

    private final List<String> data;

    public MyBaseAdapter(Context context, List<String> data) {
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_my_base, null);
            viewHolder = new ViewHolder();
            viewHolder.setTextView(convertView.findViewById(R.id.text));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.getTextView().setText(data.get(position));


        return convertView;
    }

    @Data
    public static class ViewHolder {
        private TextView textView;
    }
}
