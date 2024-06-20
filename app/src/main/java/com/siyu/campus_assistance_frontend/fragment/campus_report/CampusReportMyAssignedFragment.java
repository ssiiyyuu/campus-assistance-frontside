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
import com.siyu.campus_assistance_frontend.adapter.campus_report.CampusReportMyAssignedAdapter;
import com.siyu.campus_assistance_frontend.custom.AutoPageListView;
import com.siyu.campus_assistance_frontend.entity.PaginationQuery;
import com.siyu.campus_assistance_frontend.entity.PaginationResult;
import com.siyu.campus_assistance_frontend.entity.vo.CampusReportVO;
import com.siyu.campus_assistance_frontend.fragment.HomepageFragment;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

public class CampusReportMyAssignedFragment extends Fragment {
    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private ImageView backButton;

    private final List<CampusReportVO.Assigned> campusReports = new ArrayList<>();
    private AutoPageListView<?> campusReportListView;
    private CampusReportMyAssignedAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_campus_report_my_assigned, container, false);
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
            myAssignedAPI(query);
        });
    }

    private void initData() {
        campusReports.clear();
        campusReportListView.clearQueryPageNum();
        PaginationQuery<?> query = campusReportListView.getNextPageQuery();
        myAssignedAPI(query);
    }

    private void initComponent() {
        backButton = view.findViewById(R.id.button_back);
        campusReportListView = view.findViewById(R.id.list_campus_report);
        campusReportListView.changePageSize(AutoPageListView.MEDIUM_PAGE_SIZE);
        campusReportListView.setOnItemClickListener((parent, view, position, id) -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportDetailFragment(campusReports.get(position).getProcessId(), campusReports.get(position).getTaskId(), campusReports.get(position).getIsAssigned()));
        });
        backButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HomepageFragment());
        });
    }


    private void myAssignedAPI(PaginationQuery<?> query) {
        httpUtils.doPost("/flowable/campusReport/page/myAssigned", query, json -> {
            PaginationResult<CampusReportVO.Assigned> result = JSONObject.parseObject(json, new TypeReference<PaginationResult<CampusReportVO.Assigned>>() {});
            System.out.println("result = " + result);
            campusReportListView.updateQuery(result);
            campusReports.addAll(result.getData());
            if(adapter == null) {
                adapter = new CampusReportMyAssignedAdapter(context, campusReports);
                campusReportListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                campusReportListView.loadFinished();
            }
        });
    }
}
