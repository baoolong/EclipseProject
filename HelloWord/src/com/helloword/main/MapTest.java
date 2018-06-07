package com.helloword.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.helloword.util.WebServiceUtils;

/**
 * 百度地图和高德地图，实际测试精度的数据分析
 * @author MrRight
 */
public class MapTest {
	
	private static WebServiceUtils webServiceUtils;
	private static List<String> list=new ArrayList<String>();
	private static final String SPLIT_TAG_LAT="Latitude:";
	private static final String SPLIT_TAG_LOG="Longitude:";
	
	private static final String SRCPATH="C:\\Users\\MrRight\\Desktop\\G10_1516002241808.txt";
	private static final String USER_NAME="林子盛";
	private static final int USER_ID=170;
	
	/**
	 * 120公里每小时的话  2秒可以跑66.7米。超过66.7，要不就是异常，要不就是超速
	 */
	public static void main(String[] args) {
		saveFileByFileReaderAndWriter(SRCPATH);
//		parseListInfo();
		saveJson();
	}
	
	
	private static void parseListInfo() {
		new Thread() {
			public void run() {
				for (String string : list) {
					try {
						if(string==null||string.isEmpty()) {
							continue;
						}
						String strs[]=string.split(SPLIT_TAG_LAT);
						//01-15 15:44:02
						String time="2018-"+strs[0].trim().replace("01-15 1", "01-16 0");
						String strsinfo[]=strs[1].split(SPLIT_TAG_LOG);
						String latitude=strsinfo[0].trim();
						String longitude=strsinfo[1].trim();
						System.out.println("time:"+time+"  latitude:"+latitude+"  longitude:"+longitude);
						insert(USER_ID,USER_NAME,"TEL",longitude+","+latitude,"123","DETAIL",time);
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	
	
	
	private static void saveJson() {
		BufferedWriter bufferedWriter = null;	// 字符输出缓冲流
		String destPath="C:\\Users\\MrRight\\Desktop\\777.txt";
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(new File(destPath), true));// 以追加的形式写文件
			
			String line = "";
			for (String string : list) {
				
				if(string==null||string.isEmpty()) {
					continue;
				}
				String strs[]=string.split(SPLIT_TAG_LAT);
				String strsinfo[]=strs[1].split(SPLIT_TAG_LOG);
				String latitude=strsinfo[0].trim();
				String longitude=strsinfo[1].trim(); 
				//new BMap.Point(116.423493, 39.907445)
				line="["+longitude+", "+latitude+"],";
				
				bufferedWriter.write(line);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
			System.out.println("文件保存成功！");
			
		} catch (Exception e) {
			System.out.println("文件保存失败！");
			e.printStackTrace();
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	
	/**
	 * 使用文件字符流  另存为一个文件
	 * @param srcPath	源文件
	 */
	public static void saveFileByFileReaderAndWriter(String srcPath) {
		BufferedReader bufferedReader = null;	// 字符输入缓冲流
		try {
			bufferedReader = new BufferedReader(new FileReader(new File(srcPath)));
			String line = "";
			while((line = bufferedReader.readLine()) != null) {	// 是否读到最后
				System.out.println("Line: "+line);
				list.add(line);
			}
			System.out.println("文件读取成功！");
			
		} catch (Exception e) {
			System.out.println("文件读取失败！");
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 向服务器插入自己的位�?
	 * @param userName 用户姓名
	 * @param tel 电话
	 * @param coordinate 经纬�? 要用逗号隔开
	 * @param eventId 事件ID
	 * @param details 备注
	 * @param sendTime 发送的时间
	 */
	public static void insert(final int personId,final String userName,final String tel,
			final String coordinate,final String eventId,final String details,
			final String sendTime){
		new Thread() {
			public void run() {
				try{
					LinkedHashMap<String , Object> hashMap=new LinkedHashMap<>();
					hashMap.put("personId", personId);
					hashMap.put("userName", userName);
					hashMap.put("tel", tel);
					hashMap.put("coordinate", coordinate);
					hashMap.put("eventId", eventId);
					hashMap.put("details", details);
					hashMap.put("sendTime", sendTime);
					System.out.println("向服务器插入数据");
					if (webServiceUtils==null) {
						webServiceUtils = WebServiceUtils.getInstance();
					}
					webServiceUtils.requestEventWeb(hashMap, "insert");
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}
}
