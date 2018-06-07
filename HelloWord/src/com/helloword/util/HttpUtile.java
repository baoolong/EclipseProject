package com.helloword.util;

import java.io.File;

import org.apache.commons.codec.digest.DigestUtils;

import com.helloword.interfaces.OkHttpDownFileCallBackListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import okhttp3.Call;

public class HttpUtile {
	
	
	/**
	 * 下载文件  调用execute(callback)进行执行，传入callback则代表是异步。如果单纯的execute()则代表同步的方法调用
	 * @param downFileUrl 下载链接
	 * @param saveFileRootPath 保存路径
	 * @param okHttpListener 监听
	 */
	public static void downFile(String downFileUrl,final String saveFileRootPath,final OkHttpDownFileCallBackListener okHttpListener) {
		if (okHttpListener == null) {
			return;
		}
		if (downFileUrl==null||downFileUrl.isEmpty()) {
			return;
		}
		String fileSuffix = downFileUrl.substring(downFileUrl.lastIndexOf("."), downFileUrl.length());
		String fileName = DigestUtils.md5Hex(downFileUrl)+fileSuffix;
		OkHttpUtils
				.get()
				.url(downFileUrl)
				.build()
				.execute(
						new FileCallBack(saveFileRootPath,fileName) {
							@Override
							public void inProgress(float progress,long total, int id) {
								super.inProgress(progress, total, id);
								okHttpListener.inProgress(progress, total, id);
							}

							@Override
							public void onResponse(File arg0, int arg1) {
								okHttpListener.onResponse(arg0, arg1);
							}

							@Override
							public void onError(Call arg0, Exception arg1,int arg2) {
								okHttpListener.onError(arg0, arg1, arg2);
							}
						}
				);

	}
}
