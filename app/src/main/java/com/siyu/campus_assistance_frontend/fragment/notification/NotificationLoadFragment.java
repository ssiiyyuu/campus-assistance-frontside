package com.siyu.campus_assistance_frontend.fragment.notification;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.adapter.notification.NotificationLoadAdapter;
import com.siyu.campus_assistance_frontend.custom.AutoPageListView;
import com.siyu.campus_assistance_frontend.entity.PaginationQuery;
import com.siyu.campus_assistance_frontend.entity.PaginationResult;
import com.siyu.campus_assistance_frontend.entity.vo.NotificationVO;
import com.siyu.campus_assistance_frontend.enums.NotificationType;
import com.siyu.campus_assistance_frontend.utils.DialogUtils;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

public class NotificationLoadFragment extends Fragment {
    private HttpUtils httpUtils;

    private View view;
    private Context context;
    
    private ImageView backButton;
    private TextView headerText;

    private String type;

    private final List<NotificationVO.Table> notifications = new ArrayList<>();
    private AutoPageListView<NotificationVO.Condition> notificationListView;
    private NotificationLoadAdapter adapter;

    private NotificationLoadFragment() {
        super();
    }
    public NotificationLoadFragment(String type) {
        super();
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification_load, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        initListView();
        return view;
    }

    private void initData() {
        notifications.clear();
        notificationListView.clearQueryPageNum();
        PaginationQuery<NotificationVO.Condition> query = notificationListView.getNextPageQuery();
        pageAPI(query);
    }

    private void initListView() {
        NotificationVO.Condition condition = new NotificationVO.Condition();
        condition.setType(type);
        notificationListView.setCondition(condition);
        initData();
        notificationListView.setCallback(() -> {
            PaginationQuery<NotificationVO.Condition> query = notificationListView.getNextPageQuery();
            pageAPI(query);
        });
    }


    private void initComponent() {
        backButton = view.findViewById(R.id.button_back);
        headerText = view.findViewById(R.id.text_header);
        notificationListView = view.findViewById(R.id.list_notification);
        notificationListView.changePageSize(AutoPageListView.MEDIUM_PAGE_SIZE);
        backButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new NotificationFragment());
        });
        if(type.equals(NotificationType.SYSTEM.name())) {
            headerText.setText("系统通知");
        } else if(type.equals(NotificationType.ADMIN.name())) {
            headerText.setText("管理员通知");
        } else if(type.equals(NotificationType.COUNSELOR.name())) {
            headerText.setText("辅导员通知");
        }
        notificationListView.setOnItemClickListener((parent, view, position, id) -> {
            loadAPI(notifications.get(position).getId());
        });
    }

    private void pageAPI(PaginationQuery<NotificationVO.Condition> query) {
        httpUtils.doPost("/frontside/notification/page", query, json -> {
            PaginationResult<NotificationVO.Table> result = JSONObject.parseObject(json, new TypeReference<PaginationResult<NotificationVO.Table>>() {});
            notificationListView.updateQuery(result);
            notifications.addAll(result.getData());
            if(adapter == null) {
                adapter = new NotificationLoadAdapter(context, notifications);
                notificationListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                notificationListView.loadFinished();
            }
        });
    }

    private void loadAPI(String id) {
        httpUtils.doGet("/frontside/notification/" + id, null, json -> {
            NotificationVO.Out out = JSONObject.parseObject(json, NotificationVO.Out.class);
            DialogUtils.showContentDialog((FragmentActivity) context, "通知内容", out.getContent(), () -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new NotificationLoadFragment(type));
            });
        });
    }

}
