package com.siyu.campus_assistance_frontend.adapter.information;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.entity.vo.InformationVO;
import com.siyu.campus_assistance_frontend.enums.InformationStatus;

import java.util.List;

import lombok.Data;
import lombok.Setter;

public class InformationMineAdapter extends BaseAdapter {
    private final Context context;

    private final List<InformationVO.Table> data;

    @Setter
    private InnerCallback innerCallback;

    public InformationMineAdapter(Context context, List<InformationVO.Table> data) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_information_mine, null);
            viewHolder = new ViewHolder();
            viewHolder.setTitleView(convertView.findViewById(R.id.text_title));
            viewHolder.setStatusView(convertView.findViewById(R.id.text_status));
            viewHolder.setVisitView(convertView.findViewById(R.id.text_visits));
            viewHolder.setCategoryView(convertView.findViewById(R.id.text_category));

            viewHolder.setEditButton(convertView.findViewById(R.id.button_edit));
            viewHolder.setPublishButton(convertView.findViewById(R.id.button_publish));
            viewHolder.setOfflineButton(convertView.findViewById(R.id.button_offline));
            viewHolder.setDeletedButton(convertView.findViewById(R.id.button_delete));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        InformationVO.Table information = data.get(position);
        viewHolder.getTitleView().setText(information.getTitle());
        viewHolder.getStatusView().setText("[" + InformationStatus.valueOf(information.getStatus()).getStatus() + "]");
        viewHolder.getCategoryView().setText(information.getCategory());
        viewHolder.getVisitView().setText(String.valueOf(information.getVisits()));

        viewHolder.getEditButton().setOnClickListener(view -> innerCallback.edit(view, data.get(position), position));
        viewHolder.getPublishButton().setOnClickListener(view -> innerCallback.publish(view, data.get(position), position));
        viewHolder.getOfflineButton().setOnClickListener(view -> innerCallback.offline(view, data.get(position), position));
        viewHolder.getDeletedButton().setOnClickListener(view -> innerCallback.delete(view, data.get(position), position));

        if(InformationStatus.CREATED.name().equals(information.getStatus())) {
            viewHolder.getEditButton().setEnabled(true);
            viewHolder.getPublishButton().setEnabled(true);
            viewHolder.getOfflineButton().setEnabled(false);
            viewHolder.getDeletedButton().setEnabled(true);
        } else if(InformationStatus.PUBLISHED.name().equals(information.getStatus())) {
            viewHolder.getEditButton().setEnabled(true);
            viewHolder.getPublishButton().setEnabled(false);
            viewHolder.getOfflineButton().setEnabled(true);
            viewHolder.getDeletedButton().setEnabled(true);
        } else if(InformationStatus.OFFLINE.name().equals(information.getStatus())) {
            viewHolder.getEditButton().setEnabled(true);
            viewHolder.getPublishButton().setEnabled(true);
            viewHolder.getOfflineButton().setEnabled(false);
            viewHolder.getDeletedButton().setEnabled(true);
        } else {
            viewHolder.getEditButton().setEnabled(false);
            viewHolder.getPublishButton().setEnabled(false);
            viewHolder.getOfflineButton().setEnabled(false);
            viewHolder.getDeletedButton().setEnabled(false);
        }
        return convertView;
    }

    public interface InnerCallback {
        void edit(View view, InformationVO.Table table, int position);

        void publish(View view, InformationVO.Table table, int position);

        void offline(View view, InformationVO.Table table, int position);

        void delete(View view, InformationVO.Table table, int position);
    }
    @Data
    public static class ViewHolder {
        private TextView titleView;
        private TextView statusView;
        private TextView categoryView;
        private TextView visitView;

        private TextView editButton;
        private TextView publishButton;
        private TextView offlineButton;
        private TextView deletedButton;
    }
}
