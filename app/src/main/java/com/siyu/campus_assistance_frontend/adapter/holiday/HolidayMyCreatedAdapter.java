package com.siyu.campus_assistance_frontend.adapter.holiday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.entity.vo.HolidayVO;

import java.util.List;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Data;

public class HolidayMyCreatedAdapter extends BaseAdapter {

    private final Context context;

    private final List<HolidayVO.Created> data;

    public HolidayMyCreatedAdapter(Context context, List<HolidayVO.Created> data) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_holiday_my_created, null);
            viewHolder = new ViewHolder();
            viewHolder.setStatusView(convertView.findViewById(R.id.text_status));
            viewHolder.setTypeView(convertView.findViewById(R.id.text_type));
            viewHolder.setCreateTimeView(convertView.findViewById(R.id.text_create_time));
            viewHolder.setStartTimeView(convertView.findViewById(R.id.text_start_time));
            viewHolder.setEndTimeView(convertView.findViewById(R.id.text_end_time));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HolidayVO.Created created = data.get(position);
        viewHolder.getStatusView().setText(created.getStatus());
        viewHolder.getTypeView().setText(created.getType());
        viewHolder.getCreateTimeView().setText(LocalDateTimeUtil.format(created.getCreateTime(), "yyyy-MM-dd HH:mm"));
        viewHolder.getStartTimeView().setText(LocalDateTimeUtil.format(created.getStartTime(), "yyyy-MM-dd HH:mm"));
        viewHolder.getEndTimeView().setText(LocalDateTimeUtil.format(created.getEndTime(), "yyyy-MM-dd HH:mm"));

        return convertView;
    }

    @Data
    public static class ViewHolder {
        private TextView typeView;
        private TextView createTimeView;
        private TextView startTimeView;
        private TextView endTimeView;
        private TextView statusView;
    }
}
