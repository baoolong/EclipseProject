package com.helloword.main;

import java.io.File;

public class ChangeFilename {

	public static void main(String[] args) {
		String filename="F:\\Android Project\\应急平板\\Icon\\PNG";
		File file=new File(filename);
		File []pngFiles=file.listFiles();
		File newFile;
		for(File pngf:pngFiles) {
			if(pngf.getName().contains("图层")) {
				String newName=pngf.getName().replace("图层 ", "loading_");
				newFile=new File(filename+"\\"+newName);
				pngf.renameTo(newFile);
			}
		}
	}
}
