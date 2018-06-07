package com.helloword.interfaces;

import okhttp3.Call;

/**
 * 项目名称：IM
 * 包名：com.lgyw.im.http
 * 作者: HZQ on 2017/7/17 17:35
 * 邮箱: 583498457@qq.com
 */

public interface HttpCallBack {
    void onError(Call call, Exception e, int errorCode);
    void onResponse(String s, int resultCode);
}
