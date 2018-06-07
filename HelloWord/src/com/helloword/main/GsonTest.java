package com.helloword.main;

import com.google.gson.Gson;

public class GsonTest {

	public static void formGson() {
		GsonBean bean=new GsonBean("true","true","2.0","‘›Œﬁ√Ë ˆ","https://www.baidu.com/","xinDian.apk");
		String json=new Gson().toJson(bean);
		System.out.println(json);
	}
}

class GsonBean{
	private String OK;
	private String SureToUpdate;
	private String VersionsNub;
	private String VersionsDescribe;
	private String downFileServlet;
	private String fileName;
	
	public GsonBean(String oK, String sureToUpdate, String versionsNub, String versionsDescribe, String downFileServlet,
			String fileName) {
		super();
		OK = oK;
		SureToUpdate = sureToUpdate;
		VersionsNub = versionsNub;
		VersionsDescribe = versionsDescribe;
		this.downFileServlet = downFileServlet;
		this.fileName = fileName;
	}
	
	public String getOK() {
		return OK;
	}
	public void setOK(String oK) {
		OK = oK;
	}
	public String getSureToUpdate() {
		return SureToUpdate;
	}
	public void setSureToUpdate(String sureToUpdate) {
		SureToUpdate = sureToUpdate;
	}
	public String getVersionsNub() {
		return VersionsNub;
	}
	public void setVersionsNub(String versionsNub) {
		VersionsNub = versionsNub;
	}
	public String getVersionsDescribe() {
		return VersionsDescribe;
	}
	public void setVersionsDescribe(String versionsDescribe) {
		VersionsDescribe = versionsDescribe;
	}
	public String getDownFileServlet() {
		return downFileServlet;
	}
	public void setDownFileServlet(String downFileServlet) {
		this.downFileServlet = downFileServlet;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public String toString() {
		return "GsonBean [OK=" + OK + ", SureToUpdate=" + SureToUpdate + ", VersionsNub=" + VersionsNub
				+ ", VersionsDescribe=" + VersionsDescribe + ", downFileServlet=" + downFileServlet + ", fileName="
				+ fileName + "]";
	}
}