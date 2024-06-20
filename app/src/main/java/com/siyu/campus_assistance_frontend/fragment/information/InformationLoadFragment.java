package com.siyu.campus_assistance_frontend.fragment.information;

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

import com.alibaba.fastjson.JSONObject;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.custom.HttpImageView;
import com.siyu.campus_assistance_frontend.entity.vo.InformationVO;
import com.siyu.campus_assistance_frontend.fragment.HomepageFragment;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import cn.hutool.core.date.LocalDateTimeUtil;

public class InformationLoadFragment extends Fragment {

    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private ImageView backButton;
    private TextView headerTextView;
    private HttpImageView coverImageView;
    private TextView authorTextView;
    private TextView visitsTextView;
    private TextView contentTextView;
    private TextView publishTextView;
    private TextView categoryTextView;
    private TextView departmentTextView;
    private String informationId;

    private InformationLoadFragment() {
        super();
    }
    public InformationLoadFragment(String informationId) {
        super();
        this.informationId = informationId;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_information_load, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        initData();
        return view;
    }

    private void initData() {
        loadAPI(informationId, json -> {
            InformationVO.Out out = JSONObject.parseObject(json, InformationVO.Out.class);
            headerTextView.setText(out.getTitle());
            coverImageView.getImage(out.getCover());
            authorTextView.setText(out.getAuthor());
            visitsTextView.setText(String.valueOf(out.getVisits()));
            contentTextView.setText(out.getContent());
            publishTextView.setText(LocalDateTimeUtil.format(out.getPublishTime(), "yyyy-MM-dd"));
            categoryTextView.setText(out.getCategory());
            departmentTextView.setText(out.getDepartment());
        });
    }

    private void initComponent() {
        backButton = view.findViewById(R.id.button_back);
        headerTextView = view.findViewById(R.id.text_header);
        coverImageView = view.findViewById(R.id.image_cover);
        authorTextView = view.findViewById(R.id.text_author);
        visitsTextView = view.findViewById(R.id.text_visits);
        contentTextView = view.findViewById(R.id.text_content);
        publishTextView = view.findViewById(R.id.text_publish_time);
        categoryTextView = view.findViewById(R.id.text_category);
        departmentTextView = view.findViewById(R.id.text_department);

        backButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HomepageFragment());
        });
    }


    private void loadAPI(String id, HttpUtils.InnerCallback callback) {
        httpUtils.doGet("/frontside/information/" + id, null, callback);
    }

}
