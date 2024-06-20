package com.siyu.campus_assistance_frontend.adapter.notification;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.adapter.information.InformationMineAdapter;
import com.siyu.campus_assistance_frontend.entity.vo.NotificationVO;

import java.util.List;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Data;
import lombok.Setter;

public class NotificationLoadAdapter extends BaseAdapter {

    private final Context context;

    private final List<NotificationVO.Table> data;

    @Setter
    private InformationMineAdapter.InnerCallback innerCallback;

    public NotificationLoadAdapter(Context context, List<NotificationVO.Table> data) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_notification_load, null);
            viewHolder = new ViewHolder();
            viewHolder.setFrom(convertView.findViewById(R.id.from));
            viewHolder.setDateSend(convertView.findViewById(R.id.send_date));
            viewHolder.setRead(convertView.findViewById(R.id.read));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NotificationVO.Table table = data.get(position);
        viewHolder.getFrom().setText(table.getFrom());
        viewHolder.getDateSend().setText(LocalDateTimeUtil.format(table.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        if(table.getRead()) {
            viewHolder.getRead().setText("已读");
            viewHolder.getRead().setTextColor(Color.parseColor("#67C23A"));
        } else {
            viewHolder.getRead().setText("未读");
            viewHolder.getRead().setTextColor(Color.parseColor("#F56C6C"));
        }

        return convertView;
    }
    @Data
    public static class ViewHolder {
        private TextView from;
        private TextView dateSend;
        private TextView read;
    }
}
