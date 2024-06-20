package com.siyu.campus_assistance_frontend.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.entity.Result;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.StrUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class HttpUtils {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType PNG = MediaType.parse("image/png");
    private static final String BASE_API = "http://192.168.1.8:8084/";
    private static final String BASE_WS = "ws://192.168.1.8:8084/WebSocket";

//    private static final String BASE_API = "http://10.21.204.241:8084/";
//    private static final String BASE_WS = "ws://10.21.204.241:8084/WebSocket";

//    private static final String BASE_API = "http://10.21.73.158:8084/";
//    private static final String BASE_WS = "ws://10.21.73.158:8084/WebSocket";
    private static final long HEART_BEAT_RATE = 10 * 60 * 1000;

    private static final int MAX_RECONNECT_NUM = 20;

    private final Handler handler;

    private static volatile HttpUtils httpUtils;

    private final OkHttpClient okHttpClient;

    private final Context context;

    private WebSocket webSocket;
    private boolean isConnect = false;
    private int connectNum = 0;
    //心跳包发送时间计时
    private long sendTime = 0L;
    // 发送心跳包


    private HttpUtils(Context context) {
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new InnerInterceptor())
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
        handler = new Handler(Looper.getMainLooper());
        this.context = context.getApplicationContext();

    }

    public static HttpUtils getInstance(Context context) {
        if(httpUtils == null) {
            synchronized(HttpUtils.class) {
                if(httpUtils == null) {
                    httpUtils = new HttpUtils(context);
                }
            }
        }
        return httpUtils;
    }

    public interface InnerCallback {
        void onSuccess(String json);
    }
    public interface InnerImageCallback {
        void onSuccess(Bitmap bitmap);

        void onFailure(Bitmap bitmap);
    }
    public interface InnerWebSocketCallback {
        void onMessage(String text);
    }

    private class InnerInterceptor implements Interceptor {

        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {

            Request request = chain.request();
            //给request header添加token
            request = TokenUtils.setToken(request, context);

            Response response = chain.proceed(request);
            if(response.code() != 200 && response.code() != 101) {
                throw new IOException("Http Error");
            }
            //根据response刷新token
            String token = response.header(TokenUtils.AUTHENTICATION_HEADER);
            if(StrUtil.isNotBlank(token)) {
                TokenUtils.updateToken(token, context);
                doGet("/info", null, json -> {
                    CurrentUserUtils.updateCurrentUser(json, context);
                });
            }
            return response;
        }
    }

    public void generateCall(Request request, InnerCallback callback) {
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if(callback != null) {
                    handler.post(() -> {
                        String message = e.getMessage();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.body() != null && callback != null) {
                    Result<?> result = JSONObject.parseObject(response.body().string(), Result.class);
                    if(result.getSuccess()) {
                        handler.post(() -> {
                            callback.onSuccess(result.getData() == null ? "" : result.getData().toString());
                        });
                    } else {
                        handler.post(() -> {
                            String message = "[" + result.getStatus() + "]:" + result.getMessage();
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }


    private void generateImageCall(Request request, InnerImageCallback callback) {
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if(callback != null) {
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img);
                    handler.post(() -> callback.onFailure(bitmap));
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if(response.body() != null && callback != null) {
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    handler.post(() -> callback.onSuccess(bitmap));
                }
            }
        });
    }

    public String generateUrl(String originalUrl, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder(originalUrl);

        if(null != params && !params.isEmpty()) {
            if(!originalUrl.contains("?")) {
                stringBuilder.append("?");
            }
            params.forEach((key, value) -> {
                stringBuilder.append(key).append("=").append(value).append("&");
            });
            stringBuilder.substring(0, stringBuilder.lastIndexOf("&"));
        }

        return stringBuilder.toString();
    }

    private RequestBody generateBody(Object params) {
        String json = JSONObject.toJSONString(params);
        return RequestBody.create(json, JSON);
    }

    private MultipartBody generateFileBody(File file) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, PNG))
                .build();
    }

    public void doGetImage(String originalUrl, InnerImageCallback callback) {
        Request request = new Request.Builder()
                .get().url(originalUrl)
                .build();
        generateImageCall(request, callback);
    }

    public void doUploadFile(String originalUrl, File file, InnerCallback callback) {
        originalUrl = BASE_API + originalUrl;
        Request request = new Request.Builder()
                .post(generateFileBody(file)).url(originalUrl)
                .build();
        generateCall(request, callback);
    }

    public void doPost(String originalUrl, Object params, InnerCallback callback) {
        originalUrl = BASE_API + originalUrl;
        RequestBody body = generateBody(params);
        Request request = new Request.Builder()
                .post(body).url(originalUrl)
                .build();
        generateCall(request, callback);
    }

    public void doPut(String originalUrl, Object params, InnerCallback callback) {
        originalUrl = BASE_API + originalUrl;
        RequestBody body = generateBody(params);
        Request request = new Request.Builder()
                .put(body).url(originalUrl)
                .build();
        generateCall(request, callback);
    }

    public void doDelete(String originalUrl, Map<String, String> params, InnerCallback callback) {
        originalUrl = BASE_API + originalUrl;
        String url = generateUrl(originalUrl, params);
        Request request = new Request.Builder()
                .delete().url(url)
                .build();
        generateCall(request, callback);
    }

    public void doGet(String originalUrl, Map<String, String> params, InnerCallback callback) {
        originalUrl = BASE_API + originalUrl;
        String url = generateUrl(originalUrl, params);
        Request request = new Request.Builder()
                .get().url(url)
                .build();
        generateCall(request, callback);
    }

    public void disconnect() {
        if(webSocket == null || !isConnect) {
            return;
        }
        webSocket.cancel();
        webSocket.close(1001, "closed");
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void connect(InnerWebSocketCallback callback) {
        if(webSocket != null && isConnect) {
            return;
        }

        Request request = new Request.Builder()
                .url(BASE_WS + "/" + CurrentUserUtils.getCurrentUserId(context))
                .build();
        okHttpClient.newWebSocket(request, createWebSocketListener(callback));
    }

    public void reconnect(InnerWebSocketCallback callback) {
        if (connectNum <= MAX_RECONNECT_NUM) {
            try {
                Thread.sleep(10000);
                connect(callback);
                connectNum++;
            } catch (InterruptedException e) {
                handler.post(() -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            handler.post(() -> {
                Toast.makeText(context, "连接服务器失败", Toast.LENGTH_SHORT).show();
            });
        }
    }

    public boolean sendMessage(String message) {
        if(webSocket != null && isConnect) {
            return webSocket.send(message);
        }
        return false;
    }

    private Runnable sendHeartMessage() {
        return () -> {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                sendTime = System.currentTimeMillis();
                sendMessage("HEART MESSAGE");
            }
            handler.postDelayed(sendHeartMessage(), HEART_BEAT_RATE);
        };
    }

    private WebSocketListener createWebSocketListener(InnerWebSocketCallback callback) {
        return new WebSocketListener() {
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
                HttpUtils.this.webSocket = null;
                isConnect = false;
                if(handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosing(webSocket, code, reason);
                HttpUtils.this.webSocket = null;
                isConnect = false;
                if(handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                isConnect = false;
                if(handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                if(!StrUtil.isEmpty(t.getMessage()) && !t.getMessage().equals("Socket closed")){
                    reconnect(callback);
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                super.onMessage(webSocket, text);
                if(callback != null) {
                    callback.onMessage(text);
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
                if(callback != null) {
                    callback.onMessage(bytes.base64());
                }
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);
                HttpUtils.this.webSocket = webSocket;
                HttpUtils.this.isConnect = true;
                handler.postDelayed(sendHeartMessage(), HEART_BEAT_RATE);
            }
        };
    }

}
