package com.helloword.main;

import java.util.LinkedHashMap;

import com.helloword.util.WebServiceUtils;

public class WebService {

	
	public static void main(String[] args) {
		String gsonStr =new  WebServiceUtils().requestResouceWeb(initParameters(),"getDangersouList");
		System.out.println("result:" + gsonStr);
	}
	
	
	
	
	/**
	 * 构造请求网络的参数
	 */
	private static LinkedHashMap<String, Object> initParameters(){
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("name","");
		parameters.put("cpx", "");// 经度
		parameters.put("cpy", "");// 纬度
		parameters.put("raduis",50);
        parameters.put("typeIds", "");
        parameters.put("levelIds","");
        parameters.put("dangerType","bigDanger");
              
      
		return parameters;
	}
}
