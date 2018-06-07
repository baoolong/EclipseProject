package com.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import com.lgyw.emergency.natives.AntiDebugNative;

import android.app.Application;

public class App extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		new Thread() {
			public void run() {
				int pid = android.os.Process.myPid();
				String pkName = App.this.getPackageName();
				while(true) {
					AntiDebugNative.checkProgramStatue();
					HashSet<String> paths=getSoList(pid,pkName);
					for(String path:paths) {
						AntiDebugNative.uninstall(path);
					}
					try {
						Thread.sleep(5*60*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	
	//该函数搜索进程PID被注入的第三方so。
	private  HashSet<String> getSoList(int pid, String pkg){
	    HashSet<String> temp = new HashSet<String>();
	    File file = new File("/proc/" + pid + "/maps");
	    if (!file.exists()){
	        return temp;
	    }
	    try{
	        BufferedReader bufferedReader = new BufferedReader(
	        new InputStreamReader(new FileInputStream(file)));
	        String lineString = null;
	        while ((lineString = bufferedReader.readLine()) != null){
	            String tempString = lineString.trim();
	            if (tempString.contains("/data/data")&& !tempString.contains("/data/data/" + pkg)){
	                int index = tempString.indexOf("/data/data");
	                temp.add(tempString.substring(index));
	            }
	        } 
	        bufferedReader.close();
	    }
	    catch (FileNotFoundException e){
	        e.printStackTrace();
	    }
	    catch (IOException e){
	        e.printStackTrace();
	    }
	    return temp;
	}
}
