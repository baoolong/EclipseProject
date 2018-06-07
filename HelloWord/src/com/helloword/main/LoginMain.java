package com.helloword.main;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.helloword.Encrypt.ShaPasswordEncoder;
import com.helloword.confige.Constants;

public class LoginMain {

	public static void main(String[] args) {
		try {
			String json=userLogin("linzisheng","123456");
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				Constants.DISPATHC_NAMESPACE,"loginMethodService");
		request.addProperty("username", username);
		request.addProperty("password", encrypt(username, password));
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = request;
		HttpTransportSE ht = new HttpTransportSE(Constants.DISPATHC_URL);
		ht.call(null, envelope);
		Object ob = envelope.getResponse();
		return ob.toString();
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
}
