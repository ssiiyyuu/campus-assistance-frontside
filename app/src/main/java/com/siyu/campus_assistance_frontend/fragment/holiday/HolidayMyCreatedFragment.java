package com.siyu.campus_assistance_frontend.fragment.holiday;

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
import com.siyu.campus_assistance_frontend.adapter.holiday.HolidayMyCreatedAdapter;
import com.siyu.campus_assistance_frontend.custom.AutoPageListView;
import com.siyu.campus_assistance_frontend.entity.PaginationQuery;
import com.siyu.campus_assistance_frontend.entity.PaginationResult;
import com.siyu.campus_assistance_frontend.entity.vo.HolidayVO;
import com.siyu.campus_assistance_frontend.fragment.HomepageFragment;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

public class HolidayMyCreatedFragment extends Fragment {
    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private ImageView backButton;
    private ImageView addButton;

    private final List<HolidayVO.Created> holidays = new ArrayList<>();
    private AutoPageListView<?> holidayListView;
    private HolidayMyCreatedAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_holiday_my_created, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        initListView();
        return view;
    }

    private void initListView() {
        initData();
        holidayListView.setCallback(() -> {
            PaginationQuery<?> query = holidayListView.getNextPageQuery();
            myCreatedAPI(query);
        });
    }

    private void initData() {
        holidays.clear();
        holidayListView.clearQueryPageNum();
        PaginationQuery<?> query = holidayListView.getNextPageQuery();
        myCreatedAPI(query);
    }

    private void initComponent() {
        addButton = view.findViewById(R.id.button_add);
        backButton = view.findViewById(R.id.button_back);
        holidayListView = view.findViewById(R.id.list_holiday);
        holidayListView.changePageSize(AutoPageListView.MEDIUM_PAGE_SIZE);
        holidayListView.setOnItemClickListener((parent, view, position, id) -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayDetailFragment(holidays.get(position).getProcessId(), holidays.get(position).getTaskId()));
        });
        backButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HomepageFragment());
        });
        addButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayEditFragment());
        });
    }


    private void myCreatedAPI(PaginationQuery<?> query) {
        httpUtils.doPost("/flowable/holiday/page/myCreated", query, json -> {
            PaginationResult<HolidayVO.Created> result = JSONObject.parseObject(json, new TypeReference<PaginationResult<HolidayVO.Created>>() {});
            holidayListView.updateQuery(result);
            holidays.addAll(result.getData());
            if(adapter == null) {
                adapter = new HolidayMyCreatedAdapter(context, holidays);
                holidayListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                holidayListView.loadFinished();
            }
        });
    }
}
