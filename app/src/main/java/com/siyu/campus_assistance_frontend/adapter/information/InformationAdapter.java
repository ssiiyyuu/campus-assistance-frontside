package com.siyu.campus_assistance_frontend.adapter.information;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.custom.HttpImageView;
import com.siyu.campus_assistance_frontend.entity.vo.InformationVO;

import java.util.List;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Data;

public class InformationAdapter extends BaseAdapter {

    private final Context context;

    private final List<InformationVO.Table> data;

    public InformationAdapter(Context context, List<InformationVO.Table> data) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_information, null);
            viewHolder = new ViewHolder();
            viewHolder.setTitleView(convertView.findViewById(R.id.text_title));
            viewHolder.setPublishTimeView(convertView.findViewById(R.id.text_publish_time));
            viewHolder.setVisitView(convertView.findViewById(R.id.text_visits));
            viewHolder.setAuthorView(convertView.findViewById(R.id.text_author));
            viewHolder.setCoverView(convertView.findViewById(R.id.image_cover));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        InformationVO.Table information = data.get(position);
        viewHolder.getTitleView().setText(information.getTitle());
        viewHolder.getPublishTimeView().setText(LocalDateTimeUtil.format(information.getPublishTime(), "yyyy-MM-dd HH:mm:ss"));
        viewHolder.getAuthorView().setText(information.getAuthor());
        viewHolder.getVisitView().setText(String.valueOf(information.getVisits()));
        viewHolder.getCoverView().getImage(information.getCover());

        return convertView;
    }

    @Data
    public static class ViewHolder {
        private TextView titleView;
        private TextView publishTimeView;
        private TextView authorView;
        private TextView visitView;
        private HttpImageView coverView;
    }
}
