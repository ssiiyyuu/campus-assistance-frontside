package com.siyu.campus_assistance_frontend.fragment.holiday;

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
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.custom.HttpImageView;
import com.siyu.campus_assistance_frontend.engine.GlideEngine;
import com.siyu.campus_assistance_frontend.entity.dto.AttachmentDTO;
import com.siyu.campus_assistance_frontend.entity.vo.HolidayVO;
import com.siyu.campus_assistance_frontend.entity.vo.SysUserVO;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;

public class HolidayEditFragment extends Fragment {
    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private List<String> holidayTypes;

    private String currentHolidayType;
    private String currentAssigneeId;
    private String currentAttachmentUrl;
    private String currentBeginTime;
    private String currentEndTime;
    private ImageView backButton;
    private Spinner typeSpinner;
    private EditText reasonEditText;
    private TextView beginTimeButton;
    private TextView endTimeButton;
    private TextView addButton;
    private Spinner assigneeSpinner;
    private HttpImageView attachmentImageView;

    private List<SysUserVO.Out> assignees;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_holiday_edit, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        initComponent();
        initAdapter();
        return view;
    }

    private void initAdapter() {
        holidayTypes = Arrays.asList("事假", "病假", "其他");
        typeSpinner.setAdapter(new ArrayAdapter<>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, holidayTypes));
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentHolidayType = holidayTypes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getAssigneeAPI(json -> {
            assignees = JSONObject.parseObject(json, new TypeReference<List<SysUserVO.Out>>() {});
            assigneeSpinner.setAdapter(new ArrayAdapter<>(context,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    assignees.stream().map(SysUserVO.Out::getNickname).collect(Collectors.toList())));
        });
        assigneeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentAssigneeId = assignees.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initComponent() {
        backButton = view.findViewById(R.id.button_back);
        typeSpinner = view.findViewById(R.id.spinner_type);
        reasonEditText = view.findViewById(R.id.edit_text_reason);
        beginTimeButton = view.findViewById(R.id.button_begin_time);
        endTimeButton = view.findViewById(R.id.button_end_time);
        attachmentImageView = view.findViewById(R.id.image_attachment);
        addButton = view.findViewById(R.id.button_add);
        assigneeSpinner = view.findViewById(R.id.spinner_assignee);

        backButton.setOnClickListener(v -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyCreatedFragment());
        });
        beginTimeButton.setOnClickListener(v -> {
            new CardDatePickerDialog.Builder(context)
                    .setOnChoose("确定", aLong -> {
                        String localDateTime = LocalDateTimeUtil.format(LocalDateTimeUtil.of(aLong), "yyyy-MM-dd hh:mm:ss");
                        currentBeginTime = localDateTime;
                        beginTimeButton.setText(localDateTime);
                        return null;
                    }).build().show();
        });
        endTimeButton.setOnClickListener(v -> {
            new CardDatePickerDialog.Builder(context)
                    .setOnChoose("确定", aLong -> {
                        String localDateTime = LocalDateTimeUtil.format(LocalDateTimeUtil.of(aLong), "yyyy-MM-dd hh:mm:ss");
                        currentEndTime = localDateTime;
                        endTimeButton.setText(localDateTime);
                        return null;
                    }).build().show();
        });

        attachmentImageView.setOnClickListener(v -> {
            PictureSelector.create(this)
                    .openGallery(SelectMimeType.ofImage())
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .setMaxSelectNum(1)
                    .isCameraForegroundService(false)
                    .forResult(new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(ArrayList<LocalMedia> result) {
                            LocalMedia localMedia = result.get(0);
                            Glide.with(HolidayEditFragment.this).load(localMedia.getPath()).into(attachmentImageView);
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
            HolidayVO.Create create = new HolidayVO.Create();
            create.setType(currentHolidayType);
            create.setReason(reasonEditText.getText().toString());
            create.setStartTime(currentBeginTime);
            create.setEndTime(currentEndTime);
            create.setAssigneeId(currentAssigneeId);
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

    private void createAPI(HolidayVO.Create create) {
        httpUtils.doPost("/flowable/holiday/", create, json -> {
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyCreatedFragment());
            Toast.makeText(context, "成功申请", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadImageAPI(String path) {
        File file = new File(path);
        httpUtils.doUploadFile("/oss/img", file, json -> {
            currentAttachmentUrl = json;
        });
    }

    private void getAssigneeAPI(HttpUtils.InnerCallback callback) {
        httpUtils.doGet("/flowable/holiday/assignee/list", null, callback);
    }
}
