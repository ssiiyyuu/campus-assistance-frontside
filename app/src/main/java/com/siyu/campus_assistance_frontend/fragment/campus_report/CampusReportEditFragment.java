package com.siyu.campus_assistance_frontend.fragment.campus_report;

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
import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.custom.HttpImageView;
import com.siyu.campus_assistance_frontend.engine.GlideEngine;
import com.siyu.campus_assistance_frontend.entity.dto.AttachmentDTO;
import com.siyu.campus_assistance_frontend.entity.vo.CampusReportEventVO;
import com.siyu.campus_assistance_frontend.entity.vo.CampusReportVO;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;

public class CampusReportEditFragment extends Fragment {
    private HttpUtils httpUtils;

    private View view;
    private Context context;
    private String currentAttachmentUrl;
    private String currentEventId;

    private ImageView backButton;
    private Spinner firstEventSpinner;
    private Spinner secondEventSpinner;
    private EditText textLocationEdit;
    private EditText textReasonEdit;
    private HttpImageView attachmentImage;
    private TextView addButton;

    private List<CampusReportEventVO.Tree> firstEvents = new ArrayList<>();
    private List<CampusReportEventVO.Tree> secondEvents = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_campus_report_edit, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        initAdapter();
        return view;
    }

    private void initAdapter() {
        eventTreeAPI(json -> {
            firstEvents = JSONObject.parseArray(json, CampusReportEventVO.Tree.class);
            firstEventSpinner.setAdapter(new ArrayAdapter<>(context,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    firstEvents.stream().map(CampusReportEventVO.Tree::getName).collect(Collectors.toList())));
        });

        firstEventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondEvents = firstEvents.get(position).getChildList();
                secondEventSpinner.setAdapter(new ArrayAdapter<>(context,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        secondEvents.stream().map(CampusReportEventVO.Tree::getName).collect(Collectors.toList())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        secondEventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentEventId = secondEvents.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initComponent() {
        backButton = view.findViewById(R.id.button_back);
        firstEventSpinner = view.findViewById(R.id.spinner_first_event);
        secondEventSpinner = view.findViewById(R.id.spinner_second_event);
        textLocationEdit = view.findViewById(R.id.edit_text_location);
        textReasonEdit = view.findViewById(R.id.edit_text_reason);
        attachmentImage = view.findViewById(R.id.image_attachment);
        addButton = view.findViewById(R.id.button_add);

        backButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyCreatedFragment());
        });
        attachmentImage.setOnClickListener(v -> {
            PictureSelector.create(this)
                    .openGallery(SelectMimeType.ofImage())
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .setMaxSelectNum(1)
                    .isCameraForegroundService(false)
                    .forResult(new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(ArrayList<LocalMedia> result) {
                            LocalMedia localMedia = result.get(0);
                            Glide.with(CampusReportEditFragment.this).load(localMedia.getPath()).into(attachmentImage);
                            uploadImageAPI(localMedia.getRealPath());
                            Toast.makeText(context, "附件已上传", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onCancel() {
                            Toast.makeText(context, "取消上传", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        addButton.setOnClickListener(v -> {
            CampusReportVO.Create create = new CampusReportVO.Create();
            create.setEventId(currentEventId);
            create.setCreateLocation(textLocationEdit.getText().toString());
            create.setDescription(textReasonEdit.getText().toString());
            if(StrUtil.isNotBlank(currentAttachmentUrl)) {
                AttachmentDTO attachmentDTO = new AttachmentDTO();
                attachmentDTO.setType("图片");
                attachmentDTO.setContent(currentAttachmentUrl);
                create.setAttachments(Collections.singletonList(attachmentDTO));
            } else {
                create.setAttachments(new ArrayList<>());
            }
            createAPI(create);
        });


    }

    private void eventTreeAPI(HttpUtils.InnerCallback callback) {
        httpUtils.doGet("/flowable/campusReportEvent/tree", null, callback);
    }

    private void createAPI(CampusReportVO.Create create) {
        httpUtils.doPost("/flowable/campusReport/", create, json -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyCreatedFragment());
            Toast.makeText(context, "成功申请", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadImageAPI(String path) {
        File file = new File(path);
        httpUtils.doUploadFile("/oss/img", file, json -> {
            currentAttachmentUrl = json;
        });
    }
}
