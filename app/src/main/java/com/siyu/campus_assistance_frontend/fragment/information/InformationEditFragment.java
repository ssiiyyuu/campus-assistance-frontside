package com.siyu.campus_assistance_frontend.fragment.information;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.custom.HttpImageView;
import com.siyu.campus_assistance_frontend.engine.GlideEngine;
import com.siyu.campus_assistance_frontend.entity.ShiroRole;
import com.siyu.campus_assistance_frontend.entity.vo.CategoryVO;
import com.siyu.campus_assistance_frontend.entity.vo.InformationVO;
import com.siyu.campus_assistance_frontend.utils.CurrentUserUtils;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InformationEditFragment extends Fragment {

    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private String informationId = null;

    private ImageView backButton;
    private TextView headerTextView;
    private EditText titleEditText;
    private Spinner categorySpinner;
    private HttpImageView coverImageView;
    private EditText contentEditText;
    private TextView saveButton;

    private String currentCoverUrl;
    private CategoryVO.Table currentCategory;
    private List<CategoryVO.Table> categorys = new ArrayList<>();

    public InformationEditFragment() {
        super();
    }

    public InformationEditFragment(String informationId) {
        super();
        this.informationId = informationId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_information_edit, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        categoryListAPI(json -> {
            generateCategoryList(json);
            initAdapter();
            if(informationId != null) {
                initData();
            }
        });
        return view;
    }

    private void initData() {
        loadAPI(informationId);
    }


    private void initAdapter() {
        List<String> categoryName = categorys.stream()
                .map(CategoryVO.Table::getName)
                .collect(Collectors.toList());
        categorySpinner.setAdapter(new ArrayAdapter<>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoryName));
    }

    private void initComponent() {
        backButton = view.findViewById(R.id.button_back);
        headerTextView = view.findViewById(R.id.text_header);
        titleEditText = view.findViewById(R.id.edit_text_title);
        categorySpinner = view.findViewById(R.id.spinner_category);
        coverImageView = view.findViewById(R.id.image_cover);
        contentEditText = view.findViewById(R.id.edit_text_content);
        saveButton = view.findViewById(R.id.button_save);

        backButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationMineFragment());
        });
        headerTextView.setText(null == informationId ? "新增" : "编辑");

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = categorys.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        coverImageView.setOnClickListener(v -> {
            PictureSelector.create(this)
                    .openGallery(SelectMimeType.ofImage())
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .setMaxSelectNum(1)
                    .isCameraForegroundService(false)
                    .forResult(new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(ArrayList<LocalMedia> result) {
                            LocalMedia localMedia = result.get(0);
                            Glide.with(InformationEditFragment.this).load(localMedia.getPath()).into(coverImageView);
                            uploadCoverAPI(localMedia.getRealPath());
                            Toast.makeText(context, "图片已上传", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onCancel() {
                            Toast.makeText(context, "取消上传", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        saveButton.setOnClickListener(v -> {
            InformationVO.In in = new InformationVO.In();
            in.setCover(currentCoverUrl);
            in.setContent(contentEditText.getText().toString());
            in.setTitle(titleEditText.getText().toString());
            in.setCategoryId(currentCategory.getId());

            if(informationId == null) {
                if(currentCategory.getName().equals("校园动态")) {
                    createCampusDynamicsAPI(in);
                } else if(currentCategory.getName().equals("校园公告")) {
                    createCampusAnnouncementAPI(in);
                } else {
                    throw new RuntimeException();
                }
            } else {
                updateAPI(informationId, in);
            }

        });
    }

    private void generateCategoryList(String json) {
        List<CategoryVO.Table> list = JSONObject.parseObject(json, new TypeReference<List<CategoryVO.Table>>() {});
        categorys = list.stream()
                .filter(item -> {
                    List<ShiroRole> currentRoles = CurrentUserUtils.getCurrentRoles(context);
                    if(currentRoles == null) {
                        throw new RuntimeException();
                    }
                    List<String> roles = currentRoles.stream()
                            .map(ShiroRole::getRoleCode)
                            .collect(Collectors.toList());
                    if(roles.contains("grade_counselor") || roles.contains("department_counselor")) {
                        return item.getName().equals("校园公告") || item.getName().equals("校园动态");
                    } else if(roles.contains("student")) {
                        return item.getName().equals("校园动态");
                    } else {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    private void uploadCoverAPI(String path) {
        File file = new File(path);
        httpUtils.doUploadFile("/oss/cover", file, json -> {
            currentCoverUrl = json;
        });
    }

    private void loadAPI(String id) {
        httpUtils.doGet("/frontside/information/" + id + "/edit", null, json -> {
            InformationVO.Out out = JSONObject.parseObject(json, InformationVO.Out.class);
            titleEditText.setText(out.getTitle());
            contentEditText.setText(out.getContent());
            coverImageView.getImage(out.getCover());
            currentCoverUrl = out.getCover();
            int position = 0;
            for (int i = 0; i < categorys.size(); i++) {
                if(categorys.get(i).getId().equals(out.getCategoryId())) {
                    position = i;
                }
            }
            currentCategory = categorys.get(position);
            categorySpinner.setSelection(position);

        });
    }
    private void categoryListAPI(HttpUtils.InnerCallback callback) {
        httpUtils.doGet("/frontside/category/list", null, callback);
    }

    private void updateAPI(String id, InformationVO.In in) {
        httpUtils.doPut("/frontside/information/" + id, in, json -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationMineFragment());
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        });
    }
    private void createCampusDynamicsAPI(InformationVO.In in) {
        httpUtils.doPost("/frontside/information/campus/dynamics", in, json -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationMineFragment());
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        });
    }

    private void createCampusAnnouncementAPI(InformationVO.In in) {
        httpUtils.doPost("/frontside/information/campus/announcement", in, json -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new InformationMineFragment());
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        });
    }
}
