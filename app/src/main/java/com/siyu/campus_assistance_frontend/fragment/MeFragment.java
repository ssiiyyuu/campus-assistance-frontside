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

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.custom.HttpImageView;
import com.siyu.campus_assistance_frontend.entity.ShiroUser;
import com.siyu.campus_assistance_frontend.utils.CurrentUserUtils;

public class MeFragment extends Fragment {

    private View view;
    private Context context;

    private HttpImageView avatarImage;
    private TextView idText;
    private TextView usernameText;
    private TextView nicknameText;
    private TextView emailText;
    private TextView departmentText;
    private TextView departmentCodeText;
    private TextView roleText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);
        context = getActivity();
        initComponent();
        return view;
    }

    private void initComponent() {
        avatarImage = view.findViewById(R.id.image_avatar);
        idText = view.findViewById(R.id.text_id);
        usernameText = view.findViewById(R.id.text_username);
        nicknameText = view.findViewById(R.id.text_nickname);
        emailText = view.findViewById(R.id.text_email);
        departmentText = view.findViewById(R.id.text_department);
        departmentCodeText = view.findViewById(R.id.text_department_code);
        roleText = view.findViewById(R.id.text_role);

        ShiroUser currentUser = CurrentUserUtils.getCurrentUser(context);
        if(currentUser != null) {
            avatarImage.getImage(currentUser.getAvatar());
            idText.setText(currentUser.getId());
            usernameText.setText(currentUser.getUsername());
            nicknameText.setText(currentUser.getNickname());
            emailText.setText(currentUser.getEmail());
            departmentText.setText(currentUser.getDepartment().getFullName());
            departmentCodeText.setText(currentUser.getDepartment().getCode());
            roleText.setText(currentUser.getRoles().get(0).getRoleName());
        }
    }
}
