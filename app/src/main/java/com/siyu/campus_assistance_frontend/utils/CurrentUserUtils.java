package com.siyu.campus_assistance_frontend.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONObject;
import com.siyu.campus_assistance_frontend.entity.ShiroDepartment;
import com.siyu.campus_assistance_frontend.entity.ShiroRole;
import com.siyu.campus_assistance_frontend.entity.ShiroUser;

import java.util.List;

import cn.hutool.core.util.StrUtil;

public class CurrentUserUtils {
    private static final String USER_INFO = "UserInfo";

    public static ShiroUser getCurrentUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO, MODE_PRIVATE);
        String userJson = sharedPreferences.getString(USER_INFO, null);
        if(StrUtil.isBlank(userJson)) {
            return null;
        }
        return JSONObject.parseObject(userJson, ShiroUser.class);
    }

    public static String getCurrentUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO, MODE_PRIVATE);
        String userJson = sharedPreferences.getString(USER_INFO, null);
        if(StrUtil.isBlank(userJson)) {
            return null;
        }
        return JSONObject.parseObject(userJson, ShiroUser.class).getId();
    }

    public static List<ShiroRole> getCurrentRoles(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO, MODE_PRIVATE);
        String userJson = sharedPreferences.getString(USER_INFO, null);
        if(StrUtil.isBlank(userJson)) {
            return null;
        }
        return JSONObject.parseObject(userJson, ShiroUser.class).getRoles();
    }

    public static ShiroDepartment getCurrentDepartment(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO, MODE_PRIVATE);
        String userJson = sharedPreferences.getString(USER_INFO, null);
        if(StrUtil.isBlank(userJson)) {
            return null;
        }
        return JSONObject.parseObject(userJson, ShiroUser.class).getDepartment();
    }

    public static void updateCurrentUser(String userJson, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(USER_INFO, userJson);
        edit.apply();
    }
}
