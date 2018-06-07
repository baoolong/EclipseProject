package com.helloword.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class SplitDatas {
	
	private static final String filePath="C:\\Users\\MrRight\\Desktop\\新建文件夹";
	
	public static void main(String[] args) {
		checkFile();
	}
	
	private static void checkFile() {
		File file=new File(filePath);
		if(file.isDirectory()) {
			File [] files=file.listFiles();
			for(File fil:files) {
				splitDatas(fil);
			}
		}else {
			splitDatas(file);
		}
	}
	
	public static void splitDatas(File file) {
		BufferedReader bufferedReader = null;
		StringBuilder builder = new StringBuilder();
		BufferedWriter bufferedWriter = null;	// 字符输出缓冲流
		try {
			// 字符缓冲流, 只能读取 文本格式 和 代码编码格式一样的文件，不一样就会出乱码
//			bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
			bufferedReader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(file), "utf-8"));
			String line = "";
			while ( (line = bufferedReader.readLine()) != null) {	// 每次读一行，如果为空，代表读到了最后一行
				builder.append(line);
			}
			
			String data=builder.toString();
			String[] splitedStrs=data.split(".\\+data_synchronization");
			String destPath=file.getParentFile().getAbsolutePath()+"\\"+file.getName()+".java";
			bufferedWriter = new BufferedWriter(new FileWriter(new File(destPath), true));// 以追加的形式写文件
			for(String str:splitedStrs) {
				String dealOkData=".+data_synchronization"+str;
				System.out.println(dealOkData);
				bufferedWriter.write(dealOkData);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(bufferedReader!=null) {
					bufferedReader.close();
				}
				if(bufferedWriter!=null) {
					bufferedWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
