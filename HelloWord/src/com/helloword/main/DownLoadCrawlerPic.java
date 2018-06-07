package com.helloword.main;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.helloword.dao.CrawlerDao;
import com.helloword.interfaces.OkHttpDownFileCallBackListener;
import com.helloword.util.HttpUtile;

import okhttp3.Call;

public class DownLoadCrawlerPic {
	
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 30, 2, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	private CrawlerDao crawlerDao=new CrawlerDao();
	private final String SAVE_PIC_PATH="F:\\Media\\picture\\CrawlerPic";
	
	public DownLoadCrawlerPic() {
		File file=new File(SAVE_PIC_PATH);
		if(!file.exists()) {
			file.mkdirs();
		}
	}
	
	public static void main(String[] args) {
		DownLoadCrawlerPic loadCrawlerPic=new DownLoadCrawlerPic();
		loadCrawlerPic.startDownLoadThread();
	}

	public void startDownLoadThread() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					executor.execute(new Runnable() {
						@Override
						public void run() {
							String path=crawlerDao.searchPicPath();
							HttpUtile.downFile(path, SAVE_PIC_PATH, new OkHttpDownFileCallBackListener() {
								
								@Override
								public void onResponse(File response, int successCode) {
									System.out.println(path+"***"+successCode);
								}
								
								@Override
								public void onError(Call call, Exception e, int errorCode) {
									System.out.println("下载出错了");
								}
								
								@Override
								public void inProgress(float progress, long total, int id) {
									
								}
							});
						}
					});
					
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
