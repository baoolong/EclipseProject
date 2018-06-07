package com.helloword.Encrypt;

  
  
public class TestPasswordEncoder {   
  
    public static void main(String[] args) throws Exception {   
           
        md5(); // 使用�?单的MD5加密方式   c5009c0080499ab51e8f8f333bfdfb9957966346
           							//c5009c0080499ab51e8f8f333bfdfb9957966346
        sha_256(); // 使用256的哈希算�?(SHA)加密   
           
        sha_SHA_256(); // 使用SHA-256的哈希算�?(SHA)加密   
           
        md5_SystemWideSaltSource(); // 使用MD5再加全局加密盐加密的方式加密    
    }   
  
      
    public static void md5() {   
       // Md5PasswordEncoder md5 = new Md5PasswordEncoder();   
        // false 表示：生�?32位的Hex�?, 这也是encodeHashAsBase64�?, Acegi 默认配置; true  表示：生�?24位的Base64�?   
        //md5.setEncodeHashAsBase64(false);   
       // String pwd = md5.encodePassword("aobama", null);   
        //System.out.println("MD5: " + pwd + " len=" + pwd.length());   
    }   
  
      
    public static void sha_256() {   
        ShaPasswordEncoder sha = new ShaPasswordEncoder(256);   
        sha.setEncodeHashAsBase64(false);   
        String pwd = sha.encodePassword("aobama", "aobama");   
        System.out.println("哈希算法 256: " + pwd + " len=" + pwd.length());   
    }   
       
      
    public static void sha_SHA_256() {   
        ShaPasswordEncoder sha = new ShaPasswordEncoder();   
        sha.setEncodeHashAsBase64(false);   
        String pwd = sha.encodePassword("aobama", "aobama");    
        System.out.println("哈希算法 SHA-256: " + pwd + " len=" + pwd.length());   
    }   
       
      
    public static void md5_SystemWideSaltSource () {   
       // Md5PasswordEncoder md5 = new Md5PasswordEncoder();   
       // md5.setEncodeHashAsBase64(false);   
           
        // 使用动�?�加密盐的只�?要在注册用户的时候将第二个参数换成用户名即可   
        //String pwd = md5.encodePassword("aobama", "acegisalt");   
        //System.out.println("MD5 SystemWideSaltSource: " + pwd + " len=" + pwd.length());   
    }   
		
}  
