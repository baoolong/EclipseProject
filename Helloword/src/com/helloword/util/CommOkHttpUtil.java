package com.helloword.util;

import com.helloword.interfaces.OkHttpDownFileCallBackListener;
import com.helloword.interfaces.OkHttpFileCallBackListener;
import com.helloword.interfaces.OkHttpTxtCallBackListener;
import com.helloword.interfaces.OkhttpUploadFileCallBack;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * Created by hzq on 2016/6/15.
 */
public class CommOkHttpUtil {
	private final static int CONNECT_TIMEOUT =15;
	private final static int READ_TIMEOUT=120;
	private final static int WRITE_TIMEOUT=120;

	private static OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
				.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)//设置连接超时时间
				.cookieJar(new CookieJar() {
		private List<Cookie> cookieStore=new ArrayList<>();
		@Override
		public void saveFromResponse(HttpUrl arg0, List<Cookie> cookies) {
			cookieStore.clear();
			for (Cookie cookie : cookies) {
				System.out.println("postWithCookie is "+cookie.value());
				cookieStore.add(cookie);
			}

		}

		@Override
		public List<Cookie> loadForRequest(HttpUrl arg0) {
			List<Cookie> cookies = cookieStore;
			if (cookies!=null&&cookies.size()!=0) {
				for (Cookie cookie :cookies) {
					System.out.println("cookies is "+cookie.value());
				}
			}
			return cookies != null ? cookies : new ArrayList<Cookie>();
		}
	}).build();


	/**
	 * get请求
	 *
	 * @param url
	 *            相对路径地址
	 * @param okHttpListener
	 *            回调接口
	 */
	public static void getRequest(final String url,
								  final OkHttpTxtCallBackListener okHttpListener) {
		try {
			if (okHttpListener == null) {
				return;
			}
			OkHttpUtils.get().url(url).build().execute(new StringCallback() {

				@Override
				public void onResponse(String response, int successCode) {
					okHttpListener.onResponse(response, successCode);
				}

				@Override
				public void onError(Call call, Exception e, int errorCode) {
					okHttpListener.onError(call, e, errorCode);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 多文件上传带参数 并且支持cook管理
	 * @param url 链接
	 * @param pram 参数
	 * @param files 文件地址
	 */
	public static void upLoadFileWithCookie(final String url, Map<String, File> files, Map<String, String> pram
			, final OkhttpUploadFileCallBack uploadFileCallBack){

		MultipartBody.Builder body = new MultipartBody.Builder();
		RequestBody fileBody;
		if (files!=null&&files.size()!=0) {
			for (String key : files.keySet()) {
				fileBody=RequestBody.create(MediaType.parse("application/octet-stream") , files.get(key));
				body.addFormDataPart("file" , files.get(key).getName() , fileBody);
			}
		}
		if (pram!=null&&pram.size()!=0) {
			for (String str:pram.keySet()) {
				body.addFormDataPart(str, pram.get(str));
			}
		}
		Request request = new Request.Builder().url(url).post(body.build()).build();
		mOkHttpClient.newCall(request).enqueue(new Callback(){

			@Override
			public void onFailure(Call call, IOException e) {
				uploadFileCallBack.onFailure(call,e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				uploadFileCallBack.onResponse(call,response);
			}});
	}


	/**
	 * post请求
	 *
	 * @param url
	 *            相对路径地址
	 * @param paramsMap
	 *            参数键�?�对
	 * @param okHttpListener
	 *            回调接口
	 */
	public static void postRequest(final String url,
								   final Map<String, String> paramsMap,
								   final OkHttpTxtCallBackListener okHttpListener) {
		try {
			if (okHttpListener == null) {
				return;
			}
			OkHttpUtils.post().url(url).params(paramsMap).build()
					.execute(new StringCallback() {
						@Override
						public void onResponse(String response, int successCode) {
							okHttpListener.onResponse(response, successCode);
						}

						@Override
						public void onError(Call call, Exception e,
											int errorCode) {
							okHttpListener.onError(call, e, errorCode);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传多个文件
	 *
	 * @param url 相对地址
	 * @param fileMaps 文件表单
	 * @param okHttpListener 回调接口
	 */
	public static void upLoadFiles(final String url,
								   final Map<String, File> fileMaps,
								   final OkHttpFileCallBackListener okHttpListener) {
		if (okHttpListener == null || fileMaps == null) {
			return;
		}
		OkHttpUtils.post().url(url).files("file", fileMaps).build()
				.execute(new StringCallback() {
					@Override
					public void inProgress(float progress, long total, int id) {
						okHttpListener.inProgress(progress, total, id);
						super.inProgress(progress, total, id);
					}

					@Override
					public void onResponse(String arg0, int arg1) {
						okHttpListener.onResponse(arg0, arg1);
					}

					@Override
					public void onError(Call arg0, Exception e, int arg2) {
						okHttpListener.onError(arg0, e, arg2);
						e.printStackTrace();
					}
				});
	}





	/**
	 * 上传多个文件
	 *
	 * @param url 相对地址
	 * @param fileMaps 文件表单
	 * @param okHttpListener 回调接口
	 */
	public static void upLoadFilesWithParam(final String url,
			final Map<String, File> fileMaps,final Map<String, String> headerMap,
			final OkHttpFileCallBackListener okHttpListener) {
		if (okHttpListener == null || fileMaps == null) {
			return;
		}
		OkHttpUtils.post().url(url).params(headerMap).files("file", fileMaps).build()
			.execute(new StringCallback() {
				@Override
				public void inProgress(float progress, long total, int id) {
					okHttpListener.inProgress(progress, total, id);
					super.inProgress(progress, total, id);
				}

				@Override
				public void onResponse(String arg0, int arg1) {
					okHttpListener.onResponse(arg0, arg1);
				}

				@Override
				public void onError(Call arg0, Exception e, int arg2) {
					okHttpListener.onError(arg0, e, arg2);
					e.printStackTrace();
				}
			});
	}





	/**
	 * 上传单个文件
	 *
	 * @param url 地址
	 * @param file 文件
	 * @param okHttpListener  监听�?
	 */
	public static void upLoadFile(final String url, final File file,
								  final OkHttpFileCallBackListener okHttpListener) {
		if (okHttpListener == null) {

			return;
		}
		OkHttpUtils.post().url(url).addFile("file",file.getName(), file)
				.build().execute(new StringCallback() {
			@Override
			public void inProgress(float progress, long total, int id) {
				okHttpListener.inProgress(progress, total, id);
				super.inProgress(progress, total, id);
			}

			@Override
			public void onResponse(String arg0, int arg1) {
				okHttpListener.onResponse(arg0, arg1);
			}

			@Override
			public void onError(Call arg0, Exception e, int arg2) {
				okHttpListener.onError(arg0, e, arg2);
				e.printStackTrace();
			}
		});
	}


	/**
	 * 下载文件  调用execute(callback)进行执行，传入callback则代表是异步。如果单纯的execute()则代表同步的方法调用
	 * @param downFileUrl 下载链接
	 * @param saveFileRootPath 保存路径
	 * @param okHttpListener 监听
	 */
	public static void downFile(String downFileUrl,final String saveFileRootPath,
								final OkHttpDownFileCallBackListener okHttpListener) {
		if (okHttpListener == null) {
			return;
		}
		if (StrUtil.isEmpty(downFileUrl)) {
			return;
		}
		String fileSuffix = downFileUrl.substring(downFileUrl.lastIndexOf("."), downFileUrl.length());
		String fileName = str2Md5(downFileUrl)+fileSuffix;
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

	public static String str2Md5(String str) {
		return CiphertextUtil.passAlgorithmsCiphering(str, CiphertextUtil.MD5);
	}
}
