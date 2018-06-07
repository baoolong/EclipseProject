package com.helloword.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class EncryptFile {

	public static final String inPutFilePath="F:\\testFile";
		
	public static void main(String[] args) {
		dealFiles(inPutFilePath);
	}
	
	
	private static void dealFiles(String inPutFilePath) {
		File inFile=new File(inPutFilePath);
		if(!inFile.exists()) {
			return;
		}
		if(inFile.isFile()) {
			String outFileName="$"+inFile.getName()+".bfm";
			final String outPutFilePath=inFile.getParentFile().getAbsolutePath()+"\\"+outFileName;
			File outFile=new File(outPutFilePath);
			encryptFile(inFile,outFile);
		}else {
			File[] files=inFile.listFiles();
			for (File file : files) {
				dealFiles(file.getAbsolutePath());
			}
		}
	}
	
	
	
	/**
	 * 使用文件字节流  另存为一个文件
	 * @param inputStream
	 * @param outputStream
	 */
	public static void encryptFile(File pathIn,File pathOut) {
		OutputStream outputStream=null;
		InputStream inputStream=null;
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			inputStream=new FileInputStream(pathIn);
			outputStream=new FileOutputStream(pathOut);
			
			bufferedInputStream = new BufferedInputStream(inputStream);
			bufferedOutputStream = new BufferedOutputStream(outputStream);
			//每次读取的字节
			byte[] buffer = new byte[1024];	
			int count = 0;
			//如果=-1， 就是读完了
			while( (count = bufferedInputStream.read(buffer)) != -1 ) {	
				for(int item=0;item<buffer.length;item++){ 
					
					byte byteOr=(byte) (buffer[item]^108);
					buffer[item]=(byte) ~byteOr;
					
                }
				bufferedOutputStream.write(buffer, 0, count);
				bufferedOutputStream.flush();
			}
			System.out.println("文件保存成功！");
			closeIo(outputStream,inputStream,bufferedInputStream,bufferedOutputStream);
			pathIn.delete();
			File targetFile=new File(pathIn.getAbsolutePath());
			pathOut.renameTo(targetFile);
		} catch (Exception e) {
			System.out.println("文件保存失败！");
			closeIo(outputStream,inputStream,bufferedInputStream,bufferedOutputStream);
			e.printStackTrace();
		} 
	}
	
	
	private static void closeIo(OutputStream outputStream,InputStream inputStream,BufferedInputStream bufferedInputStream,BufferedOutputStream bufferedOutputStream) {
		try {
			if(bufferedOutputStream!=null) {
				bufferedOutputStream.close();			
			}
			if(outputStream!=null) {
				outputStream.close();
			}
			if(bufferedInputStream!=null) {
				bufferedInputStream.close();
			}
			if(inputStream!=null) {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
