package com.siyu.campus_assistance_frontend.fragment.holiday;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSONObject;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.custom.HttpImageView;
import com.siyu.campus_assistance_frontend.entity.ShiroRole;
import com.siyu.campus_assistance_frontend.entity.dto.CommentDTO;
import com.siyu.campus_assistance_frontend.entity.dto.SysUserBaseDTO;
import com.siyu.campus_assistance_frontend.entity.vo.HolidayVO;
import com.siyu.campus_assistance_frontend.enums.HolidayStatus;
import com.siyu.campus_assistance_frontend.fragment.ProcessStageFragment;
import com.siyu.campus_assistance_frontend.utils.CurrentUserUtils;
import com.siyu.campus_assistance_frontend.utils.DialogUtils;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.date.LocalDateTimeUtil;

public class HolidayDetailFragment extends Fragment {
    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private String processId = null;
    private String taskId = null;
    private Boolean isAssigned = false;
    private ImageView backButton;
    private TextView statusText;
    private TextView typeText;
    private TextView reasonText;
    private TextView startTimeText;
    private TextView endTimeTimeText;
    private HttpImageView attachmentImage;
    private TextView initiatorText;
    private TextView createTimeText;
    private TextView departmentText;
    private LinearLayout commentLayout;
    private TextView commentText;
    private LinearLayout destroyTimeLayout;
    private TextView destroyTimeText;
    private TextView stageButton;
    private TextView destroyButton;
    private TextView reportButton;
    private TextView agreeButton;
    private TextView rejectButton;

    private HolidayVO.Detail detail;
    private List<String> currentRoles;

    private HolidayDetailFragment() {
        super();
    }

    public HolidayDetailFragment(String processId, String taskId) {
        super();
        this.processId = processId;
        this.taskId = taskId;
    }
    public HolidayDetailFragment(String processId, String taskId, Boolean isAssigned) {
        super();
        this.processId = processId;
        this.isAssigned = isAssigned;
        this.taskId = taskId;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_holiday_detail, container, false);
        context = getActivity();
        httpUtils = HttpUtils.getInstance(context);
        getRoles();
        initComponent();
        initData();
        return view;
    }

    private void initData() {
    }

    private void getRoles() {
        List<ShiroRole> roles = CurrentUserUtils.getCurrentRoles(context);
        if(roles == null) {
            throw new RuntimeException();
        }
        currentRoles = roles.stream().map(ShiroRole::getRoleCode)
                .collect(Collectors.toList());
    }

    private void initComponent() {
        backButton = view.findViewById(R.id.button_back);
        statusText = view.findViewById(R.id.text_status);
        typeText = view.findViewById(R.id.text_type);
        reasonText = view.findViewById(R.id.text_reason);
        startTimeText = view.findViewById(R.id.text_start_time);
        endTimeTimeText = view.findViewById(R.id.text_end_time_time);
        attachmentImage = view.findViewById(R.id.image_attachment);
        initiatorText = view.findViewById(R.id.text_initiator);
        createTimeText = view.findViewById(R.id.text_create_time);
        departmentText = view.findViewById(R.id.text_department);
        commentLayout = view.findViewById(R.id.layout_comment);
        commentText = view.findViewById(R.id.text_comment);
        destroyTimeLayout = view.findViewById(R.id.layout_destroy_time);
        destroyTimeText = view.findViewById(R.id.text_destroy_time);
        stageButton = view.findViewById(R.id.button_stage);
        destroyButton = view.findViewById(R.id.button_destroy);
        reportButton = view.findViewById(R.id.button_report);
        agreeButton = view.findViewById(R.id.button_agree);
        rejectButton = view.findViewById(R.id.button_reject);

        detailAPI(processId);

    }

    private void reportAPI(String taskId, String comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(comment);
        httpUtils.doPost("/flowable/holiday/report/" + taskId + "/gradeCounselor", commentDTO, json -> {
            Toast.makeText(context, "上报成功", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyAssignedFragment());
        });
    }
    private void destroyAPI(String taskId) {
        httpUtils.doDelete("/flowable/holiday/" + taskId, null, json -> {
            Toast.makeText(context, "销假成功", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyCreatedFragment());
        });
    }
    private void agreeGradeAPI(String taskId) {
        httpUtils.doPost("/flowable/holiday/agree/" + taskId + "/gradeCounselor", null, json -> {
            Toast.makeText(context, "已同意休假", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyAssignedFragment());
        });
    }
    private void agreeDepartmentAPI(String taskId) {
        httpUtils.doPost("/flowable/holiday/agree/" + taskId + "/departmentCounselor", null, json -> {
            Toast.makeText(context, "已同意休假", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyAssignedFragment());
        });
    }
    private void rejectGradeAPI(String taskId, String comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(comment);
        httpUtils.doPost("/flowable/holiday/reject/" + taskId + "/gradeCounselor", commentDTO, json -> {
            Toast.makeText(context, "已驳回", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyAssignedFragment());
        });
    }
    private void rejectDepartmentAPI(String taskId, String comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(comment);
        httpUtils.doPost("/flowable/holiday/reject/" + taskId + "/departmentCounselor", commentDTO, json -> {
            Toast.makeText(context, "已驳回", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyAssignedFragment());
        });
    }

    @SuppressLint("SetTextI18n")
    private void detailAPI(String processId) {
        httpUtils.doGet("/flowable/holiday/" + processId, null, json -> {
            detail = JSONObject.parseObject(json, HolidayVO.Detail.class);

            HolidayVO.Create create = detail.getCreate();
            reasonText.setText(create.getReason());
            typeText.setText(create.getType());
            startTimeText.setText(create.getStartTime());
            endTimeTimeText.setText(create.getEndTime());
            if(create.getAttachments() != null && !create.getAttachments().isEmpty()) {
                attachmentImage.getImage(create.getAttachments().get(0).getContent());
            }

            SysUserBaseDTO initiator = detail.getInitiator();
            initiatorText.setText(initiator.getNickname());
            departmentText.setText(initiator.getDepartment().getFullName());
            createTimeText.setText(LocalDateTimeUtil.format(detail.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));

            List<CommentDTO> comments = detail.getComments();
            if(comments != null && !comments.isEmpty()) {
                commentLayout.setVisibility(View.VISIBLE);
                commentText.setText(comments.get(0).getContent() + "：");
            } else {
                commentLayout.setVisibility(View.GONE);
            }

            HolidayVO.Destroy destroy = detail.getDestroy();
            if(destroy.getDestroyTime() != null) {
                destroyTimeLayout.setVisibility(View.VISIBLE);
                destroyTimeText.setText(destroy.getDestroyTime());
            } else {
                destroyTimeLayout.setVisibility(View.GONE);
            }

            String status = detail.getStatus();
            statusText.setText(status);
            if(status.equals(HolidayStatus.APPLYING.getStatus())) {
                statusText.setBackgroundColor(Color.parseColor("#E6A23C"));
            } else if(status.equals(HolidayStatus.LEAVED.getStatus())) {
                statusText.setBackgroundColor(Color.parseColor("#F56C6C"));
            } else if(status.equals(HolidayStatus.LEAVING.getStatus())) {
                statusText.setBackgroundColor(Color.parseColor("#67C23A"));
            } else if(status.equals(HolidayStatus.REJECTED.getStatus())) {
                statusText.setBackgroundColor(Color.parseColor("#F56C6C"));
            }
            generateButtonByRole();
        });
    }

    private void generateButtonByRole() {
        String status = detail.getStatus();
        if(currentRoles.contains("student")) {
            backButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyCreatedFragment());
            });
            stageButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new ProcessStageFragment("holiday", processId, taskId));
            });
            reportButton.setVisibility(View.GONE);
            agreeButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
            if(status.equals(HolidayStatus.LEAVING.getStatus())) {
                destroyButton.setVisibility(View.VISIBLE);
                destroyButton.setOnClickListener(v -> {
                    destroyAPI(taskId);
                });
            } else {
                destroyButton.setVisibility(View.GONE);
            }

        } else if(currentRoles.contains("grade_counselor")) {
            backButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyAssignedFragment());
            });
            stageButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new ProcessStageFragment("holiday", processId, taskId, isAssigned));
            });
            destroyButton.setVisibility(View.GONE);

            if(isAssigned) {
                reportButton.setVisibility(View.GONE);
                agreeButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
            } else {
                reportButton.setVisibility(View.VISIBLE);
                agreeButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
                reportButton.setOnClickListener(v -> {
                    DialogUtils.showInputDialog((FragmentActivity) context, "上报原因", text -> {
                        reportAPI(taskId, text);
                    }, null);
                });
                agreeButton.setOnClickListener(v -> {
                    agreeGradeAPI(taskId);
                });

                rejectButton.setOnClickListener(v -> {
                    DialogUtils.showInputDialog((FragmentActivity) context, "驳回理由", text -> {
                        rejectGradeAPI(taskId, text);
                    }, null);
                });
            }

        } else if(currentRoles.contains("department_counselor")) {
            backButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new HolidayMyAssignedFragment());
            });
            stageButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new ProcessStageFragment("holiday", processId, taskId, isAssigned));
            });
            destroyButton.setVisibility(View.GONE);
            reportButton.setVisibility(View.GONE);

            if(isAssigned) {
                agreeButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
            } else {
                agreeButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
                agreeButton.setOnClickListener(v -> {
                    agreeDepartmentAPI(taskId);
                });

                rejectButton.setOnClickListener(v -> {
                    DialogUtils.showInputDialog((FragmentActivity) context, "驳回理由", text -> {
                        rejectDepartmentAPI(taskId, text);
                    }, null);
                });
            }

        }
    }

}
