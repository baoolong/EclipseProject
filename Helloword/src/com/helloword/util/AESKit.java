package com.helloword.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

import com.helloword.Encrypt.ShaPasswordEncoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;





/** * 编码工具类 
 * * 实现aes加密、解密
 *  */
public class AESKit {		
	/**
	* Logger for this class
	*/
   private static final Logger logger = Logger.getLogger(AESKit.class);
	
   public static final String IV = "0807060504030201";
   public static final String key = "8NONwyJtHesysWpM";
   
	
   /*******************************************************************
	* AES加密算法
	* @author moyun
	* 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定    此处使用AES-128-CBC加密模式，key需要为16位。
	* */

   //加密
   public static String Encrypt(String sSrc, String sKey) throws Exception {

	   if (sKey == null) {
		   System.out.print("Key为空null");
		   return null;
	   }
	   // 判断Key是否为16位
	   if (sKey.length() != 16) {
		   System.out.print("Key长度不是16位");
		   return null;
	   }
	   byte[] raw = sKey.getBytes();
		
	   SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	   Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
	   IvParameterSpec iv = new IvParameterSpec(IV.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
	   cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
	   byte[] encrypted = cipher.doFinal(sSrc.getBytes());
	   return  Base64.encode(encrypted);   
	   //return Base64.encodeBase64String(encrypted);//此处使用BAES64做转码功能，同时能起到2次
   }

	
   //解密
   public static String Decrypt(String sSrc, String sKey) throws Exception {
	   // 判断Key是否正确
	   if (sKey == null) {
		   System.out.print("Key为空null");
		   return null;
	   }
	   // 判断Key是否为16位
	   if (sKey.length() != 16) {
		   System.out.print("Key长度不是16位");
		   return null;
	   }
	   byte[] raw = sKey.getBytes("ASCII");
		
	   SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	   Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	 //  Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
	   IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
	   cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);	  
	   byte[] encrypted1 = Base64.decode(sSrc);//先用bAES64解密

	   try {
		   byte[] original = cipher.doFinal(encrypted1);
		   String originalString = new String(original,"UTF-8");
		   return originalString;
	   } catch (Exception e) {
		   logger.info(e.toString());
		   return null;
	   }
   }
   
   
   
   public static String decrypt(String encryptResultStr, String password) {
	   String decrpt = ebotongDecrypto(encryptResultStr);
	    byte[] decryptFrom = parseHexStr2Byte(decrpt);
		byte[] decryptResult = decrypt(decryptFrom, password);
		return new String(decryptResult);
   }

   public static String ebotongDecrypto(String str) {
	   try {
		   byte[] encodeByte = Base64.decode(str);
		   return new String(encodeByte);
	   }catch (IOException e) {
	   e.printStackTrace();
	   return str;
	   }

   }
   
   /**解密          
    *  * @param content  待解密内容          
    *  * @param password 解密密钥           
    * * @return          
    *  */
  
  private static byte[] decrypt(byte[] content, String password) {
	  try {   
	   KeyGenerator kgen = KeyGenerator.getInstance("AES"); 
	   SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );   
	   secureRandom.setSeed(password.getBytes());   
	   kgen.init(128, secureRandom);
	   kgen.init(128, new SecureRandom(password.getBytes()));
	   SecretKey secretKey = kgen.generateKey();
	   byte[] enCodeFormat = secretKey.getEncoded();
	   SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
	   Cipher cipher = Cipher.getInstance("AES");// 创建密码器                           
	   cipher.init(Cipher.DECRYPT_MODE, key);// 初始化                          
	   byte[] result = cipher.doFinal(content);
	   return result;
	   } catch (NoSuchAlgorithmException e) {e.printStackTrace();} 
	  catch (NoSuchPaddingException e) {e.printStackTrace();} 
	  catch (InvalidKeyException e) {e.printStackTrace();} 
	  catch (IllegalBlockSizeException e) 
	  {e.printStackTrace();} 
	  catch (BadPaddingException e) {e.printStackTrace();}
	  return null;
 
  }

	

   
   /**将16进制转换为二进制
    * @param hexStr
    * @return
    */
   public static byte[] parseHexStr2Byte(String hexStr) {
           if (hexStr.length() < 1)
                   return null;
           byte[] result = new byte[hexStr.length()/2];
           for (int i = 0;i< hexStr.length()/2; i++) {
                   int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
                   int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
                   result[i] = (byte) (high * 16 + low);
           }
           return result;
   }
   
	
   public static void main(String[] args) throws Exception {
		
	   String pwd = "坪山区";
	   String epwd = Encrypt(pwd, "8NONwyJtHesysWpM");
	   System.out.println(epwd);
	   System.out.println(Decrypt(epwd, "8NONwyJtHesysWpM"));
   }
	

   public static String encryptApp(String username, String password) {
		ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder();
		shaPasswordEncoder.setEncodeHashAsBase64(false);
		return shaPasswordEncoder.encodePassword(password, username);
	}

}