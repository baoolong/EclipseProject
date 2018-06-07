package com.helloword.bin;

import com.helloword.annotation.PraseClassMethod;

@PraseClassMethod(name = "world", age = 0, weight = 0)
public class PageInfoBin {
	private long STORENUM;
	private String address;
	private String md5Name;
	private int checkState;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMd5Name() {
		return md5Name;
	}
	public void setMd5Name(String md5Name) {
		this.md5Name = md5Name;
	}
	public int getCheckState() {
		return checkState;
	}
	public void setCheckState(int checkState) {
		this.checkState = checkState;
	}
	@Override
	public String toString() {
		return "PageInfoBin [STORENUM=" + STORENUM + ", address=" + address + ", md5Name=" + md5Name + ", checkState="
				+ checkState + "]";
	}
	
	
	public PageInfoBin(long sTORENUM, String address, String md5Name, int checkState) {
		super();
		this.STORENUM = sTORENUM;
		this.address = address;
		this.md5Name = md5Name;
		this.checkState = checkState;
	}
	public long getSTORENUM() {
		return STORENUM;
	}
	public void setSTORENUM(long sTORENUM) {
		STORENUM = sTORENUM;
	}
	public PageInfoBin() {}
	
	
}
