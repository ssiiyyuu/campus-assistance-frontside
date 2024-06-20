package com.siyu.campus_assistance_frontend.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.adapter.ProcessStageAdapter;
import com.siyu.campus_assistance_frontend.entity.ShiroRole;
import com.siyu.campus_assistance_frontend.entity.vo.FlowableVO;
import com.siyu.campus_assistance_frontend.fragment.campus_report.CampusReportDetailFragment;
import com.siyu.campus_assistance_frontend.fragment.holiday.HolidayDetailFragment;
import com.siyu.campus_assistance_frontend.utils.CurrentUserUtils;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessStageFragment extends Fragment {

    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private final List<FlowableVO.Stage> stages = new ArrayList<>();

    private ImageView backButton;
    private TextView headerText;
    private ListView stageList;
    private ProcessStageAdapter adapter;

    private String processKey = null;
    private String processId = null;
    private String taskId = null;
    private Boolean isAssigned = false;

    private ProcessStageFragment() {
        super();
    }

    public ProcessStageFragment(String processKey, String processId, String taskId) {
        super();
        this.processKey = processKey;
        this.processId = processId;
        this.taskId = taskId;
    }
    public ProcessStageFragment(String processKey, String processId, String taskId, Boolean isAssigned) {
        super();
        this.processKey = processKey;
        this.taskId = taskId;
        this.processId = processId;
        this.isAssigned = isAssigned;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_process_stage, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        initData();
        return view;
    }

    private void initData() {
        stages.clear();
        stageAPI(processId);
    }

    private void initComponent() {
        backButton = view.findViewById(R.id.button_back);
        headerText = view.findViewById(R.id.text_header);
        stageList = view.findViewById(R.id.list_stage);
        List<ShiroRole> currentRoles = CurrentUserUtils.getCurrentRoles(context);
        if(currentRoles == null) {
            throw new RuntimeException();
        }
        List<String> roles = currentRoles.stream()
                .map(ShiroRole::getRoleCode)
                .collect(Collectors.toList());
        if(processKey.equals("holiday")) {
            if(roles.contains("student")) {
                backButton.setOnClickListener(v -> {
                    FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayDetailFragment(processId, taskId));
                });
            } else if(roles.contains("grade_counselor") || roles.contains("department_counselor")) {
                backButton.setOnClickListener(v -> {
                    FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayDetailFragment(processId, taskId, isAssigned));
                });
            }
            headerText.setText("请假流程记录");
        } else if(processKey.equals("campus_report")) {
            if(roles.contains("student")) {
                backButton.setOnClickListener(v -> {
                    FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportDetailFragment(processId, taskId));
                });
            } else if(roles.contains("grade_counselor") || roles.contains("department_counselor")) {
                backButton.setOnClickListener(v -> {
                    FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportDetailFragment(processId, taskId, isAssigned));
                });
            }
            headerText.setText("校园上报流程记录");
        }

    }

    private void stageAPI(String processId) {
        httpUtils.doGet("/flowable/base/process/stage/" + processId, null, json -> {
            stages.addAll(JSONObject.parseObject(json, new TypeReference<List<FlowableVO.Stage>>() {}));
            System.out.println("stages = " + stages);
            if(adapter == null) {
                adapter = new ProcessStageAdapter(context, stages);
                stageList.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
         });
    }
}
