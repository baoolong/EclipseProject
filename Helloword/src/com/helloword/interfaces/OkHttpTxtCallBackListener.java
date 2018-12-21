package com.helloword.interfaces;

import okhttp3.Call;

/**
 * Created by hzq on 2016/6/15.
 */
public interface OkHttpTxtCallBackListener {
    public void onError(Call call, Exception e, int errorCode);
    public void onResponse(String response, int successCode);
}
