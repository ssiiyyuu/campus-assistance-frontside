package com.siyu.campus_assistance_frontend.adapter.campus_report;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.entity.vo.CampusReportVO;

import java.util.List;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Data;

public class CampusReportMyAssignedAdapter extends BaseAdapter {

    private final Context context;

    private final List<CampusReportVO.Assigned> data;

    public CampusReportMyAssignedAdapter(Context context, List<CampusReportVO.Assigned> data) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_campus_report_my_assigned, null);
            viewHolder = new ViewHolder();
            viewHolder.setEventText(convertView.findViewById(R.id.text_event));
            viewHolder.setCreateTimeText(convertView.findViewById(R.id.text_create_time));
            viewHolder.setLevelText(convertView.findViewById(R.id.text_level));
            viewHolder.setStatusText(convertView.findViewById(R.id.text_status));
            viewHolder.setAssignedText(convertView.findViewById(R.id.text_assigned));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CampusReportVO.Assigned assigned = data.get(position);
        viewHolder.getEventText().setText(assigned.getEvent());
        viewHolder.getCreateTimeText().setText(LocalDateTimeUtil.format(assigned.getCreateTime(), "yyyy-MM-dd HH:mm"));
        viewHolder.getLevelText().setText(assigned.getLevel());
        viewHolder.getStatusText().setText(assigned.getStatus());
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
        private TextView eventText;
        private TextView createTimeText;
        private TextView levelText;
        private TextView statusText;
        private TextView assignedText;
    }
}
