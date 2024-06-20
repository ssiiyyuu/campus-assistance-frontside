package com.siyu.campus_assistance_frontend.fragment.notification;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.entity.ShiroRole;
import com.siyu.campus_assistance_frontend.entity.vo.NotificationVO;
import com.siyu.campus_assistance_frontend.enums.NotificationType;
import com.siyu.campus_assistance_frontend.utils.CurrentUserUtils;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationFragment extends Fragment {

    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private TextView systemCountText;
    private TextView adminCountText;
    private RelativeLayout counselorLayout;
    private RelativeLayout adminLayout;
    private RelativeLayout systemLayout;
    private TextView counselorCountText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        initData();
        return view;
    }

    private void initData() {
        List<ShiroRole> currentRoles = CurrentUserUtils.getCurrentRoles(context);
        if(currentRoles == null) {
            throw new RuntimeException();
        }
        List<String> roles = currentRoles.stream()
                .map(ShiroRole::getRoleCode)
                .collect(Collectors.toList());

        countAPI(NotificationType.ADMIN.name(), json -> {
            int count = Integer.parseInt(json);
            if(count == 0) {
                adminCountText.setVisibility(View.GONE);
            } else {
                adminCountText.setText(json);
                adminCountText.setVisibility(View.VISIBLE);
            }
        });
        countAPI(NotificationType.SYSTEM.name(), json -> {
            int count = Integer.parseInt(json);
            if(count == 0) {
                systemCountText.setVisibility(View.GONE);
            } else {
                systemCountText.setText(json);
                systemCountText.setVisibility(View.VISIBLE);
            }
        });
        if(roles.contains("student")) {
            counselorLayout.setVisibility(View.VISIBLE);
            countAPI(NotificationType.COUNSELOR.name(), json -> {
                int count = Integer.parseInt(json);
                if(count == 0) {
                    counselorCountText.setVisibility(View.GONE);
                } else {
                    counselorCountText.setText(json);
                    counselorCountText.setVisibility(View.VISIBLE);
                }
            });
        } else {
            counselorLayout.setVisibility(View.GONE);
        }
    }

    private void initComponent() {
        systemCountText = view.findViewById(R.id.text_system_count);
        adminCountText = view.findViewById(R.id.text_admin_count);
        counselorCountText = view.findViewById(R.id.text_counselor_count);
        counselorLayout = view.findViewById(R.id.layout_counselor);
        adminLayout = view.findViewById(R.id.layout_admin);
        systemLayout = view.findViewById(R.id.layout_system);

        counselorLayout.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new NotificationLoadFragment(NotificationType.COUNSELOR.name()));
        });
        adminLayout.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new NotificationLoadFragment(NotificationType.ADMIN.name()));
        });
        systemLayout.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new NotificationLoadFragment(NotificationType.SYSTEM.name()));
        });
    }

    private void countAPI(String type, HttpUtils.InnerCallback callback) {
        NotificationVO.Condition condition = new NotificationVO.Condition();
        condition.setType(type);
        httpUtils.doPost("/frontside/notification/count", condition, callback);
    }
}
