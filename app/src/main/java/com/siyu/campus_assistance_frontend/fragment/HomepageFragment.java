package com.siyu.campus_assistance_frontend.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.adapter.information.InformationAdapter;
import com.siyu.campus_assistance_frontend.custom.AutoPageListView;
import com.siyu.campus_assistance_frontend.entity.PaginationQuery;
import com.siyu.campus_assistance_frontend.entity.PaginationResult;
import com.siyu.campus_assistance_frontend.entity.ShiroRole;
import com.siyu.campus_assistance_frontend.entity.ShiroUser;
import com.siyu.campus_assistance_frontend.entity.vo.CategoryVO;
import com.siyu.campus_assistance_frontend.entity.vo.InformationVO;
import com.siyu.campus_assistance_frontend.fragment.campus_report.CampusReportMyAssignedFragment;
import com.siyu.campus_assistance_frontend.fragment.campus_report.CampusReportMyCreatedFragment;
import com.siyu.campus_assistance_frontend.fragment.holiday.HolidayMyAssignedFragment;
import com.siyu.campus_assistance_frontend.fragment.holiday.HolidayMyCreatedFragment;
import com.siyu.campus_assistance_frontend.fragment.information.InformationLoadFragment;
import com.siyu.campus_assistance_frontend.fragment.information.InformationMineFragment;
import com.siyu.campus_assistance_frontend.utils.CurrentUserUtils;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomepageFragment extends Fragment {

    private static final String SYSTEM = "system";
    private static final String CAMPUS = "campus";

    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private TextView campusView;

    //流程
    private View holidayButton;
    private View campusReportButton;
    private View dormitoryRepairButton;

    //公告
    private TextView announcementButton;
    //动态
    private TextView dynamicButton;
    //系统公告，系统动态
    private TextView systemButton;
    //校园公告，校园到动态
    private TextView campusButton;
    //我的
    private TextView mineButton;

    private AutoPageListView<InformationVO.Condition> informationListView;
    private InformationAdapter adapter;

    private Map<String, String> categoryMap;

    private final List<InformationVO.Table> informations = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homepage, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        generateCategoryMap(json -> {
            List<CategoryVO.Table> list = JSONObject.parseObject(json, new TypeReference<List<CategoryVO.Table>>() {});
            categoryMap = list.stream()
                    .collect(Collectors.toMap(CategoryVO.Table::getName, CategoryVO.Table::getId));
            //初始查询条件
            informationListView.setCondition(generateCondition("系统公告"));
            initListView(SYSTEM);
        });
        return view;
    }

    /**
     * 初始化Component
     */
    private void initComponent() {
        campusView = view.findViewById(R.id.text_campus);
        holidayButton = view.findViewById(R.id.button_holiday);
        campusReportButton = view.findViewById(R.id.button_campus_report);
        dormitoryRepairButton = view.findViewById(R.id.button_dormitory_repair);
        announcementButton = view.findViewById(R.id.button_announcement);
        dynamicButton = view.findViewById(R.id.button_dynamic);
        systemButton = view.findViewById(R.id.button_system);
        campusButton = view.findViewById(R.id.button_campus);
        mineButton = view.findViewById(R.id.button_mine);
        ShiroUser currentUser = CurrentUserUtils.getCurrentUser(context);
        if(currentUser == null) {
            throw new RuntimeException();
        }
        campusView.setText(currentUser.getDepartment().getFullName().split("-")[0]);
        List<ShiroRole> currentRoles = currentUser.getRoles();
        List<String> roles = currentRoles.stream()
                .map(ShiroRole::getRoleCode)
                .collect(Collectors.toList());

        if(roles.contains("student")) {
            holidayButton.setVisibility(View.VISIBLE);
            campusReportButton.setVisibility(View.VISIBLE);
            dormitoryRepairButton.setVisibility(View.VISIBLE);
            holidayButton.setOnClickListener(view -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyCreatedFragment());
            });
            campusReportButton.setOnClickListener(view -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyCreatedFragment());
            });
        } else if(roles.contains("grade_counselor") || roles.contains("department_counselor")) {
            holidayButton.setVisibility(View.VISIBLE);
            campusReportButton.setVisibility(View.VISIBLE);
            dormitoryRepairButton.setVisibility(View.GONE);
            holidayButton.setOnClickListener(view -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyAssignedFragment());
            });
            campusReportButton.setOnClickListener(view -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyAssignedFragment());
            });
        } else if(roles.contains("workman")){
            holidayButton.setVisibility(View.GONE);
            campusReportButton.setVisibility(View.GONE);
            dormitoryRepairButton.setVisibility(View.VISIBLE);
        } else {
            holidayButton.setVisibility(View.GONE);
            campusReportButton.setVisibility(View.GONE);
            dormitoryRepairButton.setVisibility(View.GONE);
        }


        informationListView = view.findViewById(R.id.list_information);
        informationListView.changePageSize(AutoPageListView.SMALL_PAGE_SIZE);
        informationListView.setOnItemClickListener((parent, view, position, id) -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationLoadFragment(informations.get(position).getId()));
        });

        mineButton.setOnClickListener(view -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationMineFragment());
        });

        changeButton(campusButton, "校园公告");
        changeButton(systemButton, "系统公告");
        announcementButton.setOnClickListener(view -> {
            changeButton(campusButton, "校园公告");
            changeButton(systemButton, "系统公告");
        });
        dynamicButton.setOnClickListener(view -> {
            changeButton(campusButton, "校园动态");
            changeButton(systemButton, "系统动态");
        });
    }

    /**
     * 根据strategy初始化ListView
     * 1.初始化第一页数据
     * 2.添加对应的回调接口
     * @param strategy
     */
    private void initListView(String strategy) {
        initData(strategy);
        informationListView.setCallback(() -> {
            PaginationQuery<InformationVO.Condition> query = informationListView.getNextPageQuery();
            if(strategy.equals(SYSTEM)) {
                pageSystem(query);
            } else if(strategy.equals(CAMPUS)) {
                pageCampus(query);
            }
        });
    }

    /**
     * 根据strategy初始化第一页数据
     * @param strategy
     */
    private void initData(String strategy) {
        informations.clear();
        informationListView.clearQueryPageNum();
        PaginationQuery<InformationVO.Condition> query = informationListView.getNextPageQuery();
        if(strategy.equals(SYSTEM)) {
            pageSystem(query);
        } else if(strategy.equals(CAMPUS)) {
            pageCampus(query);
        }
    }
    
    private void changeButton(TextView button, String name) {
        button.setText(name);
        button.setOnClickListener(view -> {
            informationListView.setCondition(generateCondition(name));
            if(name.contains("系统")) {
                initListView(SYSTEM);
            } else if(name.contains("校园")) {
                initListView(CAMPUS);
            }
        });
    }
    
    private InformationVO.Condition generateCondition(String categoryName) {
        InformationVO.Condition condition = new InformationVO.Condition();
        condition.setCategoryId(categoryMap.get(categoryName));
        return condition;
    }



    /*------------------------------api------------------------------------*/



    private void generateCategoryMap(HttpUtils.InnerCallback callback) {
        httpUtils.doGet("/frontside/category/list", null, callback);
    }

    private void pageSystem(PaginationQuery<InformationVO.Condition> query) {
        httpUtils.doPost("/frontside/information/page/system", query, json -> {
            PaginationResult<InformationVO.Table> result = JSONObject.parseObject(json, new TypeReference<PaginationResult<InformationVO.Table>>() {});
            informationListView.updateQuery(result);
            informations.addAll(result.getData());
            if(adapter == null) {
                adapter = new InformationAdapter(context, informations);
                informationListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                informationListView.loadFinished();
            }
        });
    }

    private void pageCampus(PaginationQuery<InformationVO.Condition> query) {
        httpUtils.doPost("/frontside/information/page/campus", query, json -> {
            PaginationResult<InformationVO.Table> result = JSONObject.parseObject(json, new TypeReference<PaginationResult<InformationVO.Table>>() {});
            informationListView.updateQuery(result);
            informations.addAll(result.getData());
            if(adapter == null) {
                adapter = new InformationAdapter(context, informations);
                informationListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                informationListView.loadFinished();
            }
        });
    }

}
