package com.siyu.campus_assistance_frontend.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.fragment.HomepageFragment;
import com.siyu.campus_assistance_frontend.fragment.MeFragment;
import com.siyu.campus_assistance_frontend.fragment.notification.NotificationFragment;
import com.siyu.campus_assistance_frontend.service.NotificationService;
import com.siyu.campus_assistance_frontend.utils.FragmentUtils;
import com.siyu.campus_assistance_frontend.utils.HttpUtils;

public class IndexActivity extends AppCompatActivity {
    private HttpUtils httpUtils;

    private View homepageButton;
    private View notificationButton;
    private View meButton;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if("notification".equals(intent.getStringExtra("nav"))) {
            FragmentUtils.changeFragmentAllowingStateLoss(this, R.id.fragment_index, new NotificationFragment());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        httpUtils = HttpUtils.getInstance(this);
        initComponent();
        initService();
        FragmentUtils.changeFragment(this, R.id.fragment_index, new HomepageFragment());
    }

    private void initService() {
        Thread thread = new Thread(() -> {
            Intent intent = new Intent(this, NotificationService.class);
            startService(intent);
        });
        thread.start();
    }

    private void initComponent() {
        homepageButton = findViewById(R.id.button_homepage);
        notificationButton = findViewById(R.id.button_notification);
        meButton = findViewById(R.id.button_me);

        homepageButton.setOnClickListener(view -> {
            FragmentUtils.changeFragment(this, R.id.fragment_index, new HomepageFragment());
        });

        notificationButton.setOnClickListener(view -> {
            FragmentUtils.changeFragment(this, R.id.fragment_index, new NotificationFragment());
        });

        meButton.setOnClickListener(view -> {
            FragmentUtils.changeFragment(this, R.id.fragment_index, new MeFragment());
        });
    }

}
