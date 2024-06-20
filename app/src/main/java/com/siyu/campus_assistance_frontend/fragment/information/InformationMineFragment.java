package com.siyu.campus_assistance_frontend.fragment.information;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.adapter.information.InformationMineAdapter;
import com.siyu.campus_assistance_frontend.custom.AutoPageListView;
import com.siyu.campus_assistance_frontend.entity.PaginationQuery;
import com.siyu.campus_assistance_frontend.entity.PaginationResult;
import com.siyu.campus_assistance_frontend.entity.vo.InformationVO;
import com.siyu.campus_assistance_frontend.fragment.HomepageFragment;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

public class InformationMineFragment extends Fragment {

    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private final List<InformationVO.Table> informations = new ArrayList<>();

    private ImageView backButton;
    private ImageView addButton;
    private AutoPageListView<InformationVO.Condition> informationListView;
    private InformationMineAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_information_mine, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        initListView();
        return view;
    }


    private void initData() {
        informations.clear();
        informationListView.clearQueryPageNum();
        PaginationQuery<InformationVO.Condition> query = informationListView.getNextPageQuery();
        pageMine(query);
    }

    private void initListView() {
        informationListView.setCondition(new InformationVO.Condition());
        initData();
        informationListView.setCallback(() -> {
            PaginationQuery<InformationVO.Condition> query = informationListView.getNextPageQuery();
            pageMine(query);
        });
    }


    private void initComponent() {
        addButton = view.findViewById(R.id.button_add);
        backButton = view.findViewById(R.id.button_back);
        informationListView = view.findViewById(R.id.list_information);
        informationListView.changePageSize(AutoPageListView.MEDIUM_PAGE_SIZE);
        backButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HomepageFragment());
        });
        addButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationEditFragment());
        });
    }



    /*------------------------------api------------------------------------*/


    private void offlineAPI(String id) {
        httpUtils.doPut("/frontside/information/offline/" + id, null, json -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationMineFragment());
            Toast.makeText(context, "下线成功", Toast.LENGTH_SHORT).show();
        });
    }
    private void publishAPI(String id) {
        httpUtils.doPut("/frontside/information/publish/" + id, null, json -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationMineFragment());
            Toast.makeText(context, "发布成功", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteAPI(String id) {
        httpUtils.doDelete("/frontside/information/" + id, null, json -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationMineFragment());
            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
        });
    }
    private void pageMine(PaginationQuery<InformationVO.Condition> query) {
        httpUtils.doPost("/frontside/information/page/mine", query, json -> {
            PaginationResult<InformationVO.Table> result = JSONObject.parseObject(json, new TypeReference<PaginationResult<InformationVO.Table>>() {});
            informationListView.updateQuery(result);
            informations.addAll(result.getData());
            if(adapter == null) {
                adapter = new InformationMineAdapter(context, informations);
                adapter.setInnerCallback(new InformationMineAdapter.InnerCallback() {
                    @Override
                    public void edit(View view, InformationVO.Table table, int position) {
                        FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationEditFragment(table.getId()));
                    }

                    @Override
                    public void publish(View view, InformationVO.Table table, int position) {
                        publishAPI(table.getId());
                    }

                    @Override
                    public void offline(View view, InformationVO.Table table, int position) {
                        offlineAPI(table.getId());
                    }

                    @Override
                    public void delete(View view, InformationVO.Table table, int position) {
                        deleteAPI(table.getId());
                    }
                });
                informationListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                informationListView.loadFinished();
            }
        });
    }
}
