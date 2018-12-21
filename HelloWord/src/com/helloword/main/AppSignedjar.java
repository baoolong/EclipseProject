package com.helloword.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AppSignedjar {
	/**CMD命令*/
	private static final String ORDER_ENTER_DHARDDISK="cmd.exe /c d: && cd D:\\Android_SDK\\build-tools\\26.0.2\\lib && ";
	private static Runtime runtime = Runtime.getRuntime(); 
	
	private final static String intPutFileName="dmttyjt_2.2.11_release_2018.12.03_legu";
	/*************************************************综合应急*****************************************************************/
	private final static String keyStorePath="F:\\Android Project\\KeyStore\\应急项目\\GuangMinKEY";
	private final static String keyAlias="guangminyinji.key";
	private final static String keyPassword="lgyw1236987";
	/**************************************************心电项目****************************************************************/
//	private final static String keyStorePath="F:\\Android Project\\KeyStore\\心电项目\\ecg.key";
//	private final static String keyAlias="ly";
//	private final static String keyPassword="1236987";
	/*************************************************************************************************************************/
	
	/**执行签名的命令*/
	private static String ORDER_SIGNED_ORDER="java -jar apksigner.jar sign  --ks \""+keyStorePath+"\"  --ks-key-alias "+keyAlias+"  --ks-pass pass:"+keyPassword+"  --key-pass pass:"+keyPassword+"  --out "
			+ "C:\\Users\\MrRight\\Desktop\\"+intPutFileName+"_signedjar.apk  "
			+ "C:\\Users\\MrRight\\Desktop\\"+intPutFileName+".apk";
	
	
	public static void main(String [] agrs) {
		ORDER_SIGNED_ORDER=ORDER_ENTER_DHARDDISK+ORDER_SIGNED_ORDER;
		System.out.println(ORDER_SIGNED_ORDER);
		try {
			Process process=runtime.exec(ORDER_SIGNED_ORDER);
			String resoult3=getProcessResoult(process);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	private static String getProcessResoult(Process process) {
		String s="IPv4";  
	    String line = null;  
	    StringBuilder sb = new StringBuilder(); 
		BufferedReader  bufferedReader = new BufferedReader (new InputStreamReader(process.getInputStream()));  
		try {
			while ((line = bufferedReader.readLine()) != null) {  
			    sb.append(line + "\n");  
			    if (line.contains(s)) {  
			        System.out.println(line);  
			    }  
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(bufferedReader!=null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return line;
	}
}
