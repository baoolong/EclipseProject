package com.lgyw.emergency.natives;

public class AntiDebugNative {

	static {
		System.loadLibrary("jinLoadSo");
	}
	
	public native static void checkProgramStatue();
	
	//卸载加载的so库
	public static native void uninstall(String soPath);
}
