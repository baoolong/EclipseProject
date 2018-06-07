package com.helloword.util;

import com.helloword.interfaces.Http;
import com.helloword.interfaces.HttpCallBack;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * 项目名称：IM
 * 包名：com.lgyw.im.http.impl
 * 作者: HZQ on 2017/7/17 17:38
 * 邮箱: 583498457@qq.com
 */

public class HttpRequestImpl implements Http {
    private static final String TAG = "HttpRequestImpl";

    @Override
    public void httpPost(String url, final HttpCallBack httpCallBack) {
        OkHttpUtils.post()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        httpCallBack.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        httpCallBack.onResponse(s, i);
                    }
                });
    }

    @Override
    public void multiParamHttpPost(Map<String, String> params, String url, final HttpCallBack httpCallBack) {

        OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                httpCallBack.onError(call, e, i);
            }

            @Override
            public void onResponse(String s, int i) {
                httpCallBack.onResponse(s, i);
//                okHttpUtils.cancelTag(this);
            }
        });
    }

    @Override
    public void httpGet(String url, final HttpCallBack httpCallBack) {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        httpCallBack.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        httpCallBack.onResponse(s, i);
                    }
                });
    }

    @Override
    public void httpGet(String url, int timeOut, final HttpCallBack httpCallBack) {
        OkHttpUtils.get()
                .url(url)
                .build()
                .connTimeOut(timeOut)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        httpCallBack.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        httpCallBack.onResponse(s, i);
                    }
                });
    }

    @Override
    public void httpUploadFile(String token, String url, File file, final HttpCallBack httpCallBack) {
        OkHttpUtils.post()
                .url(url + "?access_token=" + token)
                .addFile("fileImg", file.getName(), file)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        httpCallBack.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        httpCallBack.onResponse(s, i);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                    }
                });
    }
}
