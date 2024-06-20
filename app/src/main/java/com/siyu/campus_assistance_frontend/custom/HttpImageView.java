package com.siyu.campus_assistance_frontend.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.siyu.campus_assistance_frontend.utils.HttpUtils;

public class HttpImageView extends androidx.appcompat.widget.AppCompatImageView {

    private final HttpUtils httpUtils;

    public HttpImageView(Context context) {
        super(context);
        httpUtils = HttpUtils.getInstance(context);
    }

    public HttpImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        httpUtils = HttpUtils.getInstance(context);
    }

    public HttpImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        httpUtils = HttpUtils.getInstance(context);
    }

    public void getImage(String url) {
        httpUtils.doGetImage(url, new HttpUtils.InnerImageCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(Bitmap bitmap) {
                setImageBitmap(bitmap);
            }
        });
    }
}
