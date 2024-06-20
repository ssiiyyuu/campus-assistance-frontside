package com.siyu.campus_assistance_frontend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.entity.dto.CommentDTO;
import com.siyu.campus_assistance_frontend.entity.vo.FlowableVO;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Data;

public class ProcessStageAdapter extends BaseAdapter {

    private final Context context;

    private final List<FlowableVO.Stage> data;

    public ProcessStageAdapter(Context context, List<FlowableVO.Stage> data) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_process_stage, null);
            viewHolder = new ViewHolder();
            viewHolder.setTaskNameText(convertView.findViewById(R.id.text_task_name));
            viewHolder.setStartTimeTest(convertView.findViewById(R.id.test_start_time));
            viewHolder.setEndTimeTest(convertView.findViewById(R.id.test_end_time));
            viewHolder.setAssigneeText(convertView.findViewById(R.id.text_assignee));
            viewHolder.setDurationText(convertView.findViewById(R.id.text_duration));
            viewHolder.setTypeComment(convertView.findViewById(R.id.comment_type));
            viewHolder.setContentComment(convertView.findViewById(R.id.comment_content));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FlowableVO.Stage stage = data.get(position);
        viewHolder.getTaskNameText().setText(stage.getName());
        viewHolder.getStartTimeTest().setText(LocalDateTimeUtil.format(stage.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
        viewHolder.getEndTimeTest().setText(LocalDateTimeUtil.format(stage.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
        viewHolder.getAssigneeText().setText(stage.getAssignee());
        viewHolder.getDurationText().setText(stage.getDuration() == null ? "" : stage.getDuration()/(1000*60*60) + "h");
        List<CommentDTO> comments = stage.getComments();
        if(comments != null && !comments.isEmpty()) {
            viewHolder.getTypeComment().setVisibility(View.VISIBLE);
            viewHolder.getContentComment().setVisibility(View.VISIBLE);
            viewHolder.getTypeComment().setText(comments.get(0).getType());
            viewHolder.getContentComment().setAdapter(new MyBaseAdapter(context,
                    comments.stream().map(CommentDTO::getContent).collect(Collectors.toList()))
            );
            setListViewHeightBasedOnChildren(viewHolder.getContentComment());
        } else {
            viewHolder.getTypeComment().setVisibility(View.GONE);
            viewHolder.getContentComment().setVisibility(View.GONE);
        }
        return convertView;
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter(); if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight()*2;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    @Data
    public static class ViewHolder {
        private TextView taskNameText;
        private TextView startTimeTest;
        private TextView endTimeTest;
        private TextView assigneeText;
        private TextView durationText;
        private TextView typeComment;
        private ListView contentComment;
    }
}
