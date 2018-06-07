package com.helloword.interfaces;

import java.io.File;
import java.util.Map;

/**
 * 项目名称：IM
 * 包名：com.lgyw.im.http
 * 作者: HZQ on 2017/7/17 17:29
 * 邮箱: 583498457@qq.com
 */

public interface Http {

    void httpPost(String url,HttpCallBack httpCallBack);
    void multiParamHttpPost(Map<String,String> params, String url, HttpCallBack httpCallBack);
    void httpGet(String url,HttpCallBack httpCallBack);
    void httpUploadFile(String token, String url, File file , HttpCallBack httpCallBack);
    void httpGet(String url,int timeOut, final HttpCallBack httpCallBack);
}
