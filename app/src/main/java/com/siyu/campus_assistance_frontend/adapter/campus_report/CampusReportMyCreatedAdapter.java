package com.siyu.campus_assistance_frontend.adapter.campus_report;

import android.content.Context;
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

public class CampusReportMyCreatedAdapter extends BaseAdapter {
    private final Context context;

    private final List<CampusReportVO.Created> data;

    public CampusReportMyCreatedAdapter(Context context, List<CampusReportVO.Created> data) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_campus_report_my_created, null);
            viewHolder = new ViewHolder();
            viewHolder.setEventText(convertView.findViewById(R.id.text_event));
            viewHolder.setCreateTimeText(convertView.findViewById(R.id.text_create_time));
            viewHolder.setLevelText(convertView.findViewById(R.id.text_level));
            viewHolder.setStatusText(convertView.findViewById(R.id.text_status));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CampusReportVO.Created created = data.get(position);
        viewHolder.getEventText().setText(created.getEvent());
        viewHolder.getCreateTimeText().setText(LocalDateTimeUtil.format(created.getCreateTime(), "yyyy-MM-dd HH:mm"));
        viewHolder.getLevelText().setText(created.getLevel());
        viewHolder.getStatusText().setText(created.getStatus());

        return convertView;
    }

    @Data
    public static class ViewHolder {
        private TextView eventText;
        private TextView createTimeText;
        private TextView levelText;
        private TextView statusText;
    }
}
