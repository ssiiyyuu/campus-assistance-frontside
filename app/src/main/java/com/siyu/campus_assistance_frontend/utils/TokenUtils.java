package com.siyu.campus_assistance_frontend.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import cn.hutool.core.util.StrUtil;
import okhttp3.Request;
import okhttp3.Response;

public class TokenUtils {

    public static final String AUTHENTICATION_HEADER = "Authorization";

    public static void updateToken(String token, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AUTHENTICATION_HEADER, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(AUTHENTICATION_HEADER, token);
        edit.apply();
    }

    public static void updateToken(Response response, Context context) {
        updateToken(response.header(AUTHENTICATION_HEADER), context);
    }

    private static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AUTHENTICATION_HEADER, MODE_PRIVATE);
        return sharedPreferences.getString(AUTHENTICATION_HEADER, null);
    }

    public static Request setToken(Request request, Context context) {
        String token = getToken(context);
        if(StrUtil.isNotBlank(token)) {
            return request.newBuilder()
                    .header(AUTHENTICATION_HEADER, token)
                    .build();
        }
        return request;
    }

}
