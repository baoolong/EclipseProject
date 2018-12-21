package com.helloword.main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.gson.reflect.TypeToken;
import com.helloword.Encrypt.ShaPasswordEncoder;
import com.helloword.confige.Constants;
import com.helloword.interfaces.OkhttpUploadFileCallBack;
import com.helloword.util.AESKit;
import com.helloword.util.CommOkHttpUtil;
import com.helloword.util.WebServiceUtils;

import okhttp3.Call;
import okhttp3.Response;

public class LoginMain {

	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 10000, 2, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	
	private static final boolean isLoopRequest=false;
	
	private static final String KEY="8NONwyJtHesysWpM";
	
	public static void main(String[] args) {
		
//		if(isLoopRequest) {
//			loopRequest();
//		}else {
//			normalRequest();
//		}
		UploadResouce();
//		getDetailClearPaper("51");
	}
	
	
	/**
	 *            偏好参数
	 * @param context
	 *            上下文对�?
	 * @param username
	 *            用户�?
	 * @param password
	 *            密码
	 * @return
	 * @throws Exception
	 */
	public static String userLogin( String username,String password) throws Exception {
		
		SoapObject request = new SoapObject(
				Constants.DISPATHC_NAMESPACE,"bindingUserAccountDeviceCode");
//		SoapObject request = new SoapObject(
//				Constants.DISPATHC_NAMESPACE,"loginMethodService");
		request.addProperty("username", AESKit.Encrypt(username, KEY));
		request.addProperty("password", AESKit.Encrypt(encrypt(username, password),KEY));
		request.addProperty("deviceCode", "Asc46NiwFbr7f9bUirew");
		request.addProperty("versionName", "2.2.8");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = request;
		HttpTransportSE ht = new HttpTransportSE(Constants.DISPATHC_URL);
		ht.call(null, envelope);
		Object ob = envelope.getResponse();
		return ob.toString();
	}
	
	
	private static void normalRequest() {
		try {
			String json = userLogin("lck","654321");
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void loopRequest() {
		while(true) {
			if(executor.getActiveCount()>=9888) {
				continue;
			}
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String json = userLogin("xzl","lgyw1236987");
						System.out.println(json);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	
	/**
	 * 应�?�平台的加密方法
	 * 
	 * @param username 用户�?
	 * @param password 密码
	 * @return
	 */
	private static String encrypt(String username, String password) {
		ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder();
		shaPasswordEncoder.setEncodeHashAsBase64(false);
		return shaPasswordEncoder.encodePassword(password, username);
	}
	
	
	/**
	 * 上传申请资料
	 */
	private static void UploadResouce(){
		final Map<String, File> fileMaps = new HashMap<>();
		final Map<String, String> headerMap = new HashMap<>();
		
		File file=new File("F:\\javatest.java") ;
		String filename=file.getName();
		fileMaps.put(filename, file);
		
		headerMap.put("UuserID", "666");
		headerMap.put("CuserID", "999");
		headerMap.put("AdressBook", "");
		headerMap.put("tittle", "我要请假");
		headerMap.put("time", "2018-12-20 10.20");
		headerMap.put("Cptoid", "");
		//内容
		headerMap.put("ron", "领导  我要请假半天，请领导批准");
		//内容
		headerMap.put("upOnStr", "");
		//boolean isMessageNotify=messageNotify.isChecked();
		boolean isMessageNotify=false;
		//是否短信通知
		headerMap.put("type", isMessageNotify?"1":"0");
		//是否短信通知
		headerMap.put("sendType", "2");

		CommOkHttpUtil.upLoadFileWithCookie(Constants.RELEDOCUMENTURL, fileMaps, headerMap, new OkhttpUploadFileCallBack() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) {
				System.out.println("response is:"+response);
			}
		});
	}
	
	
	/**
	 * 获取单步审批流程的信�?
	 * @param id g
	 */
	private static void  getDetailClearPaper(final String id){
		new Thread() {
			@Override
			public void run() {
				WebServiceUtils webServiceUtil=new WebServiceUtils();
				LinkedHashMap< String, Object> hashMap=new LinkedHashMap<>();
				hashMap.put("id", id);
				String gsonStr=webServiceUtil.requestApprovalWeb(hashMap, "getDetailClearPaper");
				if (gsonStr==null||gsonStr.trim().equals("")) {
					//返回数据为空或没有返回数据时,即加载失败 //handler.sendEmptyMessage(3);
					System.out.println("getDetailClearPaper,gsonStr is null");
					return;
				}
				System.out.println("gsonStr is "+gsonStr);
			}
		}.start();
	}
}
