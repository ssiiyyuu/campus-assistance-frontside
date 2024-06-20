package com.siyu.campus_assistance_frontend.fragment.campus_report;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.adapter.campus_report.CampusReportMyCreatedAdapter;
import com.siyu.campus_assistance_frontend.custom.AutoPageListView;
import com.siyu.campus_assistance_frontend.entity.PaginationQuery;
import com.siyu.campus_assistance_frontend.entity.PaginationResult;
import com.siyu.campus_assistance_frontend.entity.vo.CampusReportVO;
import com.siyu.campus_assistance_frontend.fragment.HomepageFragment;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

public class CampusReportMyCreatedFragment extends Fragment {
    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private ImageView backButton;
    private ImageView addButton;

    private final List<CampusReportVO.Created> campusReports = new ArrayList<>();
    private AutoPageListView<?> campusReportListView;

    private CampusReportMyCreatedAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_campus_report_my_created, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        initListView();
        return view;
    }

    private void initListView() {
        initData();
        campusReportListView.setCallback(() -> {
            PaginationQuery<?> query = campusReportListView.getNextPageQuery();
            myCreatedAPI(query);
        });
    }

    private void initData() {
        campusReports.clear();
        campusReportListView.clearQueryPageNum();
        PaginationQuery<?> query = campusReportListView.getNextPageQuery();
        myCreatedAPI(query);
    }

    private void initComponent() {
        addButton = view.findViewById(R.id.button_add);
        backButton = view.findViewById(R.id.button_back);
        campusReportListView = view.findViewById(R.id.list_campus_report);
        campusReportListView.changePageSize(AutoPageListView.MEDIUM_PAGE_SIZE);
        campusReportListView.setOnItemClickListener((parent, view, position, id) -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportDetailFragment(campusReports.get(position).getProcessId(), campusReports.get(position).getTaskId()));
        });
        backButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HomepageFragment());
        });
        addButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportEditFragment());
        });
    }


    private void myCreatedAPI(PaginationQuery<?> query) {
        httpUtils.doPost("/flowable/campusReport/page/myCreated", query, json -> {
            PaginationResult<CampusReportVO.Created> result = JSONObject.parseObject(json, new TypeReference<PaginationResult<CampusReportVO.Created>>() {});
            campusReportListView.updateQuery(result);
            campusReports.addAll(result.getData());
            if(adapter == null) {
                adapter = new CampusReportMyCreatedAdapter(context, campusReports);
                campusReportListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                campusReportListView.loadFinished();
            }
        });
    }
}
