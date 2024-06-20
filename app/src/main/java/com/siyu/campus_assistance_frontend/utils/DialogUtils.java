package com.siyu.campus_assistance_frontend.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.custom.HttpImageView;
import com.siyu.campus_assistance_frontend.engine.GlideEngine;

import java.util.ArrayList;
import java.util.List;

public class DialogUtils {

    public interface InputCallback {
        void click(String text);
    }
    public interface SpinnerCallback {
        void click(int position);
    }

    public interface ImageCallback {
        void uploadImage(String path);
    }

    public interface InputAndSpinnerCallback {
        void click(String input, int spinnerPosition);
    }

    public interface ContentCallback {
        void click();
    }
    public static void showInputDialog(Activity activity, String title, InputCallback positiveCallback, InputCallback negativeCallback) {
        EditText editText = new EditText(activity);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(activity);
        dialog.setTitle(title).setView(editText);
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            if(positiveCallback != null) {
                positiveCallback.click(editText.getText().toString());
            }
        });
        dialog.setNegativeButton("取消", (dialog1, which) -> {
            if(negativeCallback != null) {
                negativeCallback.click(editText.getText().toString());
            }
        });
        dialog.show();
    }

    public static void showSpinnerDialog(Activity activity, String title, List<String> list, SpinnerCallback positiveCallback, SpinnerCallback negativeCallback) {
        Spinner spinner = new Spinner(activity);
        spinner.setAdapter(new ArrayAdapter<>(activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list));
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(activity);
        dialog.setTitle(title).setView(spinner);
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            if(positiveCallback != null) {
                positiveCallback.click(spinner.getSelectedItemPosition());
            }
        });
        dialog.setNegativeButton("取消", (dialog1, which) -> {
            if(negativeCallback != null) {
                negativeCallback.click(spinner.getSelectedItemPosition());
            }
        });
        dialog.show();
    }

    public static void showContentDialog(Activity activity, String title, String content, ContentCallback callback) {
        TextView textView = new TextView(activity);
        textView.setText(content);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(activity);
        dialog.setTitle(title).setView(textView);
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            if(callback != null) {
                callback.click();
            }
        });
        dialog.show();
    }

    public static void showInputAndSpinnerDialog(Activity activity, String title, String inputLabel, String spinnerLabel, List<String> spinnerList, InputAndSpinnerCallback positiveCallback, InputAndSpinnerCallback negativeCallback) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_input_and_spinner, null);
        TextView inputLabelText = view.findViewById(R.id.text_input_label);
        EditText textInputEdit = view.findViewById(R.id.edit_text_input);
        TextView spinnerLabelText = view.findViewById(R.id.text_spinner_label);
        Spinner inputSpinner = view.findViewById(R.id.spinner_input);
        inputLabelText.setText(inputLabel);
        spinnerLabelText.setText(spinnerLabel);
        inputSpinner.setAdapter(new ArrayAdapter<>(activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerList));
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(activity);
        dialog.setTitle(title);
        dialog.setView(view);
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            if(positiveCallback != null) {
                positiveCallback.click(textInputEdit.getText().toString(), inputSpinner.getSelectedItemPosition());
            }
        });
        dialog.setNegativeButton("取消", (dialog1, which) -> {
            if(negativeCallback != null) {
                positiveCallback.click(textInputEdit.getText().toString(), inputSpinner.getSelectedItemPosition());
            }
        });
        dialog.show();
    }

    public static void showInputAndImageDialog(Activity activity, String title, String inputLabel, String imageLabel, ImageCallback imageCallback, InputCallback positiveCallback, InputCallback negativeCallback) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_input_and_image, null);
        TextView inputLabelText = view.findViewById(R.id.text_input_label);
        EditText textInputEdit = view.findViewById(R.id.edit_text_input);
        TextView imageLabelText = view.findViewById(R.id.text_image_label);
        HttpImageView attachmentImageView = view.findViewById(R.id.image_attachment);
        inputLabelText.setText(inputLabel);
        imageLabelText.setText(imageLabel);
        if(imageCallback != null) {
            attachmentImageView.setOnClickListener(v -> {
                PictureSelector.create(activity)
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .setMaxSelectNum(1)
                        .isCameraForegroundService(false)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                LocalMedia localMedia = result.get(0);
                                Glide.with(activity).load(localMedia.getPath()).into(attachmentImageView);
                                imageCallback.uploadImage(localMedia.getRealPath());
                                Toast.makeText(activity, "附件已上传", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onCancel() {
                                Toast.makeText(activity, "取消上传", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }

        AlertDialog.Builder dialog =
                new AlertDialog.Builder(activity);
        dialog.setTitle(title);
        dialog.setView(view);
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            if(positiveCallback != null) {
                positiveCallback.click(textInputEdit.getText().toString());
            }
        });
        dialog.setNegativeButton("取消", (dialog1, which) -> {
            if(negativeCallback != null) {
                negativeCallback.click(textInputEdit.getText().toString());
            }
        });
        dialog.show();
    }
}
