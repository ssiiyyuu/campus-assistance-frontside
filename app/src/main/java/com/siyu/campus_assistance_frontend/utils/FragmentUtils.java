package com.siyu.campus_assistance_frontend.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FragmentUtils {

    public static void changeFragment(Context context, int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commit();
    }
    public static void changeFragmentAllowingStateLoss(Context context, int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }
}
