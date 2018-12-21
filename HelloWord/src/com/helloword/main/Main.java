package com.helloword.main;

import java.util.HashMap;
import java.util.Map;

import com.helloword.Encrypt.ShaPasswordEncoder;
import com.helloword.util.AESKit;

public class Main {

	public static void main(String[] args) {
//		//GuiContorl contorl=new GuiContorl();
//		//contorl.showView();
		Crawler crawler=new Crawler();
//		//crawler.setCountLinsenter(contorl);
		crawler.start();
	}
	
private static Map<String,String> maps=new HashMap<>();
	

	
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
