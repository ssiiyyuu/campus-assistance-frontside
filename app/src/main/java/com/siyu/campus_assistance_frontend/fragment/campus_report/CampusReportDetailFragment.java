package com.siyu.campus_assistance_frontend.fragment.campus_report;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.siyu.campus_assistance_frontend.entity.dto.AttachmentDTO;
import com.siyu.campus_assistance_frontend.entity.dto.CommentDTO;
import com.siyu.campus_assistance_frontend.entity.dto.SysUserBaseDTO;
import com.siyu.campus_assistance_frontend.entity.vo.CampusReportVO;
import com.siyu.campus_assistance_frontend.entity.vo.SysUserVO;
import com.siyu.campus_assistance_frontend.fragment.ProcessStageFragment;
import com.siyu.campus_assistance_frontend.utils.CurrentUserUtils;
import com.siyu.campus_assistance_frontend.utils.DialogUtils;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.date.LocalDateTimeUtil;

public class CampusReportDetailFragment extends Fragment {
    private HttpUtils httpUtils;

    private View view;
    private Context context;

    private String processId = null;
    private String taskId = null;
    private Boolean isAssigned = false;
    private ImageView backButton;
    private TextView statusText;
    private TextView eventText;
    private TextView levelText;
    private TextView descriptionText;
    private TextView locationText;
    private HttpImageView createAttachmentImage;
    private TextView initiatorText;
    private TextView createTimeText;
    private LinearLayout transactCommentLayout;
    private TextView commentText;
    private LinearLayout transactAttachmentLayout;
    private HttpImageView transactAttachmentImage;
    private TextView stageButton;
    private TextView delegateButton;
    private TextView handoverButton;
    private TextView rejectButton;
    private TextView transactButton;

    private List<SysUserVO.Out> delegators = new ArrayList<>();
    private List<SysUserVO.Out> handovers = new ArrayList<>();
    private String currentAttachmentUrl = null;

    private CampusReportVO.Detail detail;
    private List<String> currentRoles;

    private CampusReportDetailFragment() {
        super();
    }

    public CampusReportDetailFragment(String processId, String taskId) {
        super();
        this.processId = processId;
        this.taskId = taskId;
    }
    public CampusReportDetailFragment(String processId, String taskId, Boolean isAssigned) {
        super();
        this.processId = processId;
        this.isAssigned = isAssigned;
        this.taskId = taskId;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_campus_report_detail, container, false);
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
        eventText = view.findViewById(R.id.text_event);
        levelText = view.findViewById(R.id.text_level);
        descriptionText = view.findViewById(R.id.text_description);
        locationText = view.findViewById(R.id.text_location);
        createAttachmentImage = view.findViewById(R.id.image_create_attachment);
        initiatorText = view.findViewById(R.id.text_initiator);
        createTimeText = view.findViewById(R.id.text_create_time);
        transactCommentLayout = view.findViewById(R.id.layout_transact_comment);
        commentText = view.findViewById(R.id.text_comment);
        transactAttachmentLayout = view.findViewById(R.id.layout_transact_attachment);
        transactAttachmentImage = view.findViewById(R.id.image_transact_attachment);
        stageButton = view.findViewById(R.id.button_stage);
        delegateButton = view.findViewById(R.id.button_delegate);
        handoverButton = view.findViewById(R.id.button_handover);
        rejectButton = view.findViewById(R.id.button_reject);
        transactButton = view.findViewById(R.id.button_transact);

        detailAPI(processId);

    }

    @SuppressLint("SetTextI18n")
    private void detailAPI(String processId) {
        httpUtils.doGet("/flowable/campusReport/" + processId, null, json -> {
            detail = JSONObject.parseObject(json, CampusReportVO.Detail.class);
            CampusReportVO.Create create = detail.getCreate();
            levelText.setText(create.getLevel());
            eventText.setText(create.getEvent());
            locationText.setText(create.getCreateLocation());
            descriptionText.setText(create.getDescription());
            if(create.getAttachments() != null && !create.getAttachments().isEmpty()) {
                createAttachmentImage.getImage(create.getAttachments().get(0).getContent());
            }

            SysUserBaseDTO initiator = detail.getInitiator();
            initiatorText.setText(initiator.getNickname());
            createTimeText.setText(LocalDateTimeUtil.format(detail.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));


            CampusReportVO.Transact transact = detail.getTransact();
            if(transact.getComment() != null) {
                transactCommentLayout.setVisibility(View.VISIBLE);
                commentText.setText(transact.getComment().getContent());
            } else {
                transactCommentLayout.setVisibility(View.GONE);
            }
            if(transact.getAttachments() != null && !transact.getAttachments().isEmpty()) {
                transactAttachmentLayout.setVisibility(View.VISIBLE);
                transactAttachmentImage.getImage(transact.getAttachments().get(0).getContent());
            } else {
                transactAttachmentLayout.setVisibility(View.GONE);
            }

            String status = detail.getStatus();
            statusText.setText(status);
            generateButtonByRole();
        });
    }

    private void generateButtonByRole() {
        if(currentRoles.contains("student")) {
            backButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyCreatedFragment());
            });
            stageButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new ProcessStageFragment("campus_report", processId, taskId));
            });
            rejectButton.setVisibility(View.GONE);
            delegateButton.setVisibility(View.GONE);
            handoverButton.setVisibility(View.GONE);
            transactButton.setVisibility(View.GONE);

        } else if(currentRoles.contains("grade_counselor")) {
            backButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyAssignedFragment());
            });
            stageButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new ProcessStageFragment("campus_report", processId, taskId, isAssigned));
            });

            rejectButton.setVisibility(View.GONE);
            delegateButton.setVisibility(View.GONE);
            if(isAssigned) {
                handoverButton.setVisibility(View.GONE);
                transactButton.setVisibility(View.GONE);
            } else {
                handoverButton.setVisibility(View.VISIBLE);
                transactButton.setVisibility(View.VISIBLE);
                handoverButton.setOnClickListener(v -> {
                    getHandoverAPI(json -> {
                        handovers = JSONObject.parseArray(json, SysUserVO.Out.class);
                        DialogUtils.showInputAndSpinnerDialog((FragmentActivity) context, "移交", "移交原因：", "移交人员：",
                                handovers.stream().map(SysUserVO.Out::getNickname).collect(Collectors.toList()),
                                (input, position) -> {
                                    handoverAPI(taskId, handovers.get(position).getId(), input);
                                }, null);
                    });
                });
                transactButton.setOnClickListener(v -> {
                    DialogUtils.showInputAndImageDialog((FragmentActivity) context, "办理", "办理意见", "办理附件",
                            this::uploadImageAPI,
                            text -> {
                                CampusReportVO.Transact transact = new CampusReportVO.Transact();
                                CommentDTO commentDTO = new CommentDTO();
                                commentDTO.setContent(text);
                                ArrayList<AttachmentDTO> attachmentDTOS = new ArrayList<>();
                                if(currentAttachmentUrl != null) {
                                    AttachmentDTO attachmentDTO = new AttachmentDTO();
                                    attachmentDTO.setContent(currentAttachmentUrl);
                                    attachmentDTOS.add(attachmentDTO);
                                }
                                transact.setComment(commentDTO);
                                transact.setAttachments(attachmentDTOS);
                                transactGradeAPI(taskId, transact);
                            }, null);
                });

            }

        } else if(currentRoles.contains("department_counselor")) {
            backButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyAssignedFragment());
            });
            stageButton.setOnClickListener(v -> {
                FragmentUtils.changeFragment(context, R.id.fragment_index, new ProcessStageFragment("campus_report", processId, taskId, isAssigned));
            });
            handoverButton.setVisibility(View.GONE);

            if(isAssigned) {
                transactButton.setVisibility(View.GONE);
                delegateButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
            } else {
                transactButton.setVisibility(View.VISIBLE);
                delegateButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
                transactButton.setOnClickListener(v -> {
                    DialogUtils.showInputAndImageDialog((FragmentActivity) context, "办理", "办理意见", "办理附件",
                        this::uploadImageAPI,
                        text -> {
                            CampusReportVO.Transact transact = new CampusReportVO.Transact();
                            CommentDTO commentDTO = new CommentDTO();
                            commentDTO.setContent(text);
                            ArrayList<AttachmentDTO> attachmentDTOS = new ArrayList<>();
                            if(currentAttachmentUrl != null) {
                                AttachmentDTO attachmentDTO = new AttachmentDTO();
                                attachmentDTO.setContent(currentAttachmentUrl);
                                attachmentDTOS.add(attachmentDTO);
                            }
                            transact.setComment(commentDTO);
                            transact.setAttachments(attachmentDTOS);
                            transactDepartmentAPI(taskId, transact);
                        }, null);
                });
                delegateButton.setOnClickListener(v -> {
                    getDelegatorAPI(json -> {
                        delegators = JSONObject.parseArray(json, SysUserVO.Out.class);
                        DialogUtils.showSpinnerDialog((FragmentActivity) context, "委派人员",
                            delegators.stream().map(SysUserVO.Out::getNickname).collect(Collectors.toList()),
                            position -> {
                                delegateAPI(taskId, delegators.get(position).getId());
                            }, null);
                    });
                });
                rejectButton.setOnClickListener(v -> {
                    DialogUtils.showInputDialog((FragmentActivity) context, "驳回理由", text -> {
                        rejectDepartmentAPI(taskId, text);
                    }, null);
                });
            }

        }
    }

    private void getDelegatorAPI(HttpUtils.InnerCallback callback) {
        httpUtils.doGet("/flowable/campusReport/delegator/list", null, callback);
    }
    private void getHandoverAPI(HttpUtils.InnerCallback callback) {
        httpUtils.doGet("/flowable/campusReport/handover/list", null, callback);
    }
    private void handoverAPI(String taskId, String handoverId, String comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(comment);
        httpUtils.doPost("/flowable/campusReport/handover/" + taskId + "/gradeCounselor/" + handoverId, commentDTO, json -> {
            Toast.makeText(context, "成功移交", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyAssignedFragment());
        });
    }
    private void delegateAPI(String taskId, String delegateId) {
        httpUtils.doPost("/flowable/campusReport/delegate/" + taskId + "/departmentCounselor/" + delegateId, null, json -> {
            Toast.makeText(context, "成功委派", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyAssignedFragment());
        });
    }
    private void transactGradeAPI(String taskId, CampusReportVO.Transact transact) {
        httpUtils.doPost("/flowable/campusReport/transact/" + taskId + "/gradeCounselor", transact, json -> {
            Toast.makeText(context, "已办理", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyAssignedFragment());
        });
    }
    private void transactDepartmentAPI(String taskId, CampusReportVO.Transact transact) {
        httpUtils.doPost("/flowable/campusReport/transact/" + taskId + "/departmentCounselor", transact, json -> {
            Toast.makeText(context, "已办理", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyAssignedFragment());
        });
    }
    private void rejectDepartmentAPI(String taskId, String comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(comment);
        httpUtils.doPost("/flowable/campusReport/reject/" + taskId + "/departmentCounselor", commentDTO, json -> {
            Toast.makeText(context, "已驳回", Toast.LENGTH_SHORT).show();
            FragmentUtils.changeFragment(context, R.id.fragment_index, new CampusReportMyAssignedFragment());
        });
    }

    private void uploadImageAPI(String path) {
        File file = new File(path);
        httpUtils.doUploadFile("/oss/img", file, json -> {
            currentAttachmentUrl = json;
        });
    }
}
