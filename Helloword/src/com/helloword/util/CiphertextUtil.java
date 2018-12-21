package com.helloword.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * CiphertextUtil
 *
 * @author ysj
 */
public class CiphertextUtil {
    public static final String MD5 = "MD5";
    public static final String SHA_1 = "SHA-1";
    public static final String SHA_256 = "SHA-256";
    private static final char[] CH_HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
        '9', 'A', 'B', 'C', 'D', 'E', 'F'};
 
    /**
     * 加密字符�?
     * 
     * @param sourceStr    �?要加密目标字符串
     * @param algorithmsName 算法名称(�?:MD5,SHA-1,SHA-256)
     * @return
     */
    public static String passAlgorithmsCiphering(String sourceStr,String algorithmsName){
        String password = "";
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithmsName);
            // 使用指定byte[]更新摘要
            md.update(sourceStr.getBytes());
            // 完成计算，返回结果数�?
            byte[] b = md.digest();
            password = byteArrayToHex(b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return password;
    }
 
    /**
     * 将字节数组转为十六进制字符串
     *
     * @param bytes
     * @return 返回16进制字符�?
     */
    private static String byteArrayToHex(byte[] bytes) {
        // �?个字节占8位，�?个十六进制字符占4位；十六进制字符数组的长度为字节数组长度的两�?
        char[] chars = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            // 取字节的�?4�?
            chars[index++] = CH_HEX[b >>> 4 & 0xf];
            // 取字节的�?4�?
            chars[index++] = CH_HEX[b & 0xf];
        }
        return new String(chars);
    }
}
