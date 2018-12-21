package com.helloword.interfaces;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 *
 * Created by Mrdong on 2017/3/7.
 */

public interface OkhttpUploadFileCallBack {
    public  void onFailure(Call call, IOException e);
    public  void onResponse(Call call, Response response);
}
