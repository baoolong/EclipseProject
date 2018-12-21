package com.helloword.interfaces;

import java.io.File;

import okhttp3.Call;

/**
 * Created by hzq on 2016/6/15.
 */
public interface OkHttpDownFileCallBackListener {

    public void onError(Call call, Exception e, int errorCode);
    public void onResponse(File response, int successCode);
    public void inProgress(float progress, long total, int id);
}
