package com.helloword.main;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.helloword.bin.GsonBean;
import com.helloword.util.Base64;


public class CommonTest {

	public static void formGson() {
		GsonBean bean=new GsonBean("true","true","2.0","��������","https://www.baidu.com/","xinDian.apk");
		String json=new Gson().toJson(bean);
		System.out.println(json);
	}
	
	
	
	public static void main(String[] args) {
		//parseJson();
		String ss=new String("1111");
		changeString(ss);
		System.out.println(ss);
	}
	
	
	private static void changeString(String ss) {
		ss=ss+"ddrebgtr";
	}
		
	
	
	private static void unicodeTest() {
		String ss="{f@Expression\\default\\0.gif中国}";
		ss=ss.replace("{","%7B").replace("}","%7D").replace(":","%3A").replace(",","%2C").replace("\'","%27").replace("\"","%22").replace("[","%5B").replace("]","%5D").replace("=","%3D").replace(" ","%20").replace(">","%3E").replace("<","%3C").replace("?","%3F").replace("\\","%5C").replace("@","%40");
		System.out.println(ss=StringEscapeUtils.escapeJava(ss));
		System.out.println(ss);
	}
	
	
	public static String getFirstKey(String s, int index) {
		return s.split("\\|")[index];
	}
	
	
	private void linkedhasmapTest() {
		LinkedHashMap<String,String> linkedHashMap=new LinkedHashMap<>(5, 0.75f, true);
		linkedHashMap.put("11", "11");
		linkedHashMap.put("22", "22");
		linkedHashMap.put("33", "33");
		linkedHashMap.put("44", "44");
		linkedHashMap.put("55", "55");
		loopLinkedHashMap(linkedHashMap);
		linkedHashMap.get("33");
		loopLinkedHashMap(linkedHashMap);
		linkedHashMap.put("22","2-2");
		loopLinkedHashMap(linkedHashMap);
	}
	
	
	
	public static void loopLinkedHashMap(LinkedHashMap<String, String> linkedHashMap){
	    Set<Map.Entry<String, String>> set = linkedHashMap.entrySet();
	    Iterator<Map.Entry<String, String>> iterator = set.iterator();
	    
	    while (iterator.hasNext()){
	        System.out.print(iterator.next() + "\t");
	    }
	    System.out.println();
	}
	
	
	static void parseJson() {
		Gson gson=new Gson();
		String json="{\"status\":true,\"isLeader\":\"false\",\"msg\":\"1\"}";
		Type type=new TypeToken<Map<String,Object>>(){}.getType();
		Map<String,Object> map=gson.fromJson(json, type);
		final String leaderKey="isLeader";
		String leaderState= (String) map.get(leaderKey);
		System.out.println(leaderState);
	}
}