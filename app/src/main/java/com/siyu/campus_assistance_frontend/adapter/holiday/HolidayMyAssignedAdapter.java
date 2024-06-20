package com.siyu.campus_assistance_frontend.adapter.holiday;

import android.content.Context;
import android.graphics.Color;
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

public class HolidayMyAssignedAdapter extends BaseAdapter {

    private final Context context;

    private final List<HolidayVO.Assigned> data;

    public HolidayMyAssignedAdapter(Context context, List<HolidayVO.Assigned> data) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_holiday_my_assigned, null);
            viewHolder = new HolidayMyAssignedAdapter.ViewHolder();
            viewHolder.setTypeText(convertView.findViewById(R.id.text_type));
            viewHolder.setCreateTimeText(convertView.findViewById(R.id.text_create_time));
            viewHolder.setInitiatorText(convertView.findViewById(R.id.text_initiator));
            viewHolder.setStartTimeText(convertView.findViewById(R.id.text_start_time));
            viewHolder.setEndTimeText(convertView.findViewById(R.id.text_end_time));
            viewHolder.setStatusText(convertView.findViewById(R.id.text_status));
            viewHolder.setAssignedText(convertView.findViewById(R.id.text_assigned));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HolidayVO.Assigned assigned = data.get(position);
        viewHolder.getTypeText().setText(assigned.getType());
        viewHolder.getCreateTimeText().setText(LocalDateTimeUtil.format(assigned.getCreatTime(), "yyyy-MM-dd HH:mm"));
        viewHolder.getInitiatorText().setText(assigned.getInitiator());
        viewHolder.getStartTimeText().setText(LocalDateTimeUtil.format(assigned.getStartTime(), "yyyy-MM-dd HH:mm"));
        viewHolder.getEndTimeText().setText(LocalDateTimeUtil.format(assigned.getEndTime(), "yyyy-MM-dd HH:mm"));
        viewHolder.getStatusText().setText(assigned.getStatus());
        if(assigned.getIsAssigned()) {
            viewHolder.getAssignedText().setText("已办");
            viewHolder.getAssignedText().setTextColor(Color.parseColor("#67C23A"));
        } else {
            viewHolder.getAssignedText().setText("代办");
            viewHolder.getAssignedText().setTextColor(Color.parseColor("#F56C6C"));
        }

        return convertView;
    }

    @Data
    public static class ViewHolder {
        private TextView typeText;
        private TextView createTimeText;
        private TextView initiatorText;
        private TextView startTimeText;
        private TextView endTimeText;
        private TextView statusText;
        private TextView assignedText;
    }
}
