package com.helloword.util;

import java.io.File;

public class ChromeDriverUtile {

//	public WebDriver openChrome() {
//		WebDriver dr = new ChromeDriver();
//		return dr;
//	}
//
//	public void closeChrome(WebDriver dr) {
//		dr.quit();
//	}
//	
//	public void scaleChromeMaxSize(WebDriver dr) {
//		dr.manage().window().maximize();
//	}
//	
//	public void scaleCromeCustomSize(WebDriver dr,int width,int height) {
//		dr.manage().window().setSize(new Dimension(width, height));
//	}
//	
//	public void openHtmlByUrl(WebDriver dr,String url) {
//		dr.get(url);
//	}
//	
//	public void openHtmlBySrc(WebDriver dr,File file) {
//		String completePath="file:///"+file.getAbsolutePath();
//		openHtmlByUrl(dr,completePath);
//	}
//	
//	public void printTitleAndUrl(WebDriver dr) {
//		System.out.printf("title of current page is %s\n", dr.getTitle());
//		System.out.printf("url of current page is %s\n", dr.getCurrentUrl());
//	}
//	
//	public void backOrForword(WebDriver dr,boolean isBack) {
//		if(isBack) {
//			dr.navigate().back();
//		}else {
//			dr.navigate().forward();
//		}
//	}
//	
//	public void executJavaScript(WebDriver dr,String script,WebElement element) {
//		if(element!=null) {
//			((JavascriptExecutor)dr).executeScript(script, element);
//		}else {
//			//在页面上直接执行js
//			((JavascriptExecutor)dr).executeScript(script);
//		}
//	}
}
