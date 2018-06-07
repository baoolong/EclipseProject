package com.helloword.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AppSignedjar {
	/***/
	private static final String ORDER_ENTER_DHARDDISK="cmd.exe /c d: && cd D:\\Android_SDK\\build-tools\\26.0.2\\lib && ";
	
	/**执行签名的命令*/
	private static String ORDER_SIGNED_ORDER="java -jar apksigner.jar sign  --ks \"#?#\"  --ks-key-alias @?@  --ks-pass pass:lgyw1236987  --key-pass pass:lgyw1236987  --out "
			+ "C:\\Users\\MrRight\\Desktop\\*?*_signedjar.apk  "
			+ "C:\\Users\\MrRight\\Desktop\\*?*.apk";
	

	private static String intPutFileName="ftyjt_2.2.4_release_2018.06.01_legu";
	private static String keyStorePath="F:\\Android Project\\KeyStore\\应急项目\\GuangMinKEY";
	private static String keyAlias="guangminyinji.key";
	
	private static Runtime runtime = Runtime.getRuntime(); 
	
	public static void main(String [] agrs) {
		signedApp();
	}
	
	
	private static void signedApp() {
		ORDER_SIGNED_ORDER=ORDER_ENTER_DHARDDISK+ORDER_SIGNED_ORDER.replace("*?*", intPutFileName);
		ORDER_SIGNED_ORDER=ORDER_ENTER_DHARDDISK+ORDER_SIGNED_ORDER.replace("#?#", keyStorePath);
		ORDER_SIGNED_ORDER=ORDER_ENTER_DHARDDISK+ORDER_SIGNED_ORDER.replace("@?@", keyAlias);
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
