package com.siyu.campus_assistance_frontend.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.entity.vo.LoginVO;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;

    private EditText usernameEditText;

    private EditText passwordEditText;

    private HttpUtils httpUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        httpUtils = HttpUtils.getInstance(this);
        initComponent();
    }

    @SuppressLint("SetTextI18n")
    private void initComponent() {
        loginButton = findViewById(R.id.button_login);
        usernameEditText = findViewById(R.id.edit_text_username);
        passwordEditText = findViewById(R.id.edit_text_password);

        loginButton.setOnClickListener(view -> login(new LoginVO(usernameEditText.getText().toString(), passwordEditText.getText().toString())));
    }

    private void login(LoginVO login) {
        String url = "/login";
        httpUtils.doPost(url, login, json -> {
            Toast.makeText(this, json, Toast.LENGTH_SHORT).show();
            Intent to = new Intent(this, IndexActivity.class);
            startActivity(to);
        });
    }

}
