package com.helloword.main;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.helloword.annotation.PraseClassMethod;
import com.helloword.bin.PageInfoBin;
import com.helloword.dao.CrawlerDao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@PraseClassMethod(name = "world", age = 0, weight = 0)
public class Crawler {
	
	private static final String savePath="F:\\Crawler\\";
	private static final int limitCount=2;
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 3, 2, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	//private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(limitCount);
	private ExecutorService daoThreadPool = Executors.newFixedThreadPool(6);
	private CrawlerDao  crawlerDao=new CrawlerDao();
	private Map<Long,PageInfoBin> storeMap=new HashMap<>();

	
	//main函数
    public  void start() {
        String url = "http://pic.yesky.com/378/625177378.shtml";
        //保存到本地的网页地址
        storeMap.put(99999l, new PageInfoBin(99999l,url, "", 0));
        Save_Html(storeMap.get(99999l));
        controlThread();
    }
    
    
    private  String getFileName(String url){
    	return  DigestUtils.md5Hex(url);
    }
    
	
    
    //将抓取的网页变成html文件，保存在本地
    public  void Save_Html(PageInfoBin  infoBin) {
    	String url=infoBin.getAddress();
    	String fileName=getFileName(url);
    	File dest = new File(savePath + fileName+".html");
    	infoBin.setCheckState(1);
        infoBin.setMd5Name(fileName);
    	
    	//为字节输入流加缓冲
        BufferedInputStream bis = null ;
        //为字节输出流加缓冲
        BufferedOutputStream bos= null;
        //接收字节输入流
        InputStream is= null;
        //字节输出流
        FileOutputStream fos = null;
        try {
        	fos = new FileOutputStream(dest);
            URL temp = new URL(url);
            URLConnection uc = temp.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (iPad; U; CPU OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");
            is = temp.openStream();
            
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(fos);
    
            int length;
            byte[] bytes = new byte[1024*20];
            while((length = bis.read(bytes, 0, bytes.length)) != -1){
                fos.write(bytes, 0, length);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("openStream流错误，跳转get流");
            //如果上面的那种方法解析错误  那么就用下面这一种方法解析
            FileOutputStream out = null;
            try{
                Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                .timeout(3000) 
                .get();
                
                if(!dest.exists())
                    dest.createNewFile();
                out=new FileOutputStream(dest,false);
                out.write(doc.toString().getBytes("utf-8"));
            }catch (Exception E) {
            	afterGetAvalidUrl(infoBin);
                System.out.println("get流错误，请检查网址是否正确"+url);
                if(dest.exists()) 
                	dest.delete();
            }finally {
				try {
					if(out!=null)
						out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
        }finally {            
			try {
				if( bis!=null)
					bis .close();
				if( bos!=null)
					bos.close();
				if( is!=null)
					is.close();
				if( fos!=null)
					fos .close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
        if(dest.exists()&&infoBin!=null)
    		Get_Localhtml(dest,infoBin);
    }
    
    
    private void afterGetAvalidUrl(PageInfoBin bin){
    	crawlerDao.upDataPageInfo(bin);
    	storeMap.remove(bin.getSTORENUM());
    	bin=null;
    }
    
    
    
    /**解析本地的html
     * 
     * @param path 路径
     */
    public  void Get_Localhtml(File file,PageInfoBin infoBin) {
        //读取本地html的路径
        try{
            if(file.isFile()){
	            //下面开始解析本地的html
	            Document doc = Jsoup.parse(file, "UTF-8");
	            Elements linkMid = doc.getElementsByClass("l_effect_img_mid");
	            Elements linkShow = doc.getElementsByClass("show");
	            Elements linkXgtj = doc.getElementsByClass("xgtjtj");
	            Elements linkbottom = doc.getElementsByClass("bottom_show");
	            
	            
	            //分离出html下<a>...</a>之间的所有东西
	            Elements links = linkShow.select("a[href]");
	            links.addAll(linkXgtj.select("a[href]"));
	            links.addAll(linkbottom.select("a[href]"));
	            
	            // 扩展名为.png的图片
	            Elements pics = linkMid.select("img[src$=.png]");
	            // 扩展名为.jpg的图片
	            pics.addAll(linkMid.select("img[src$=.jpg]"));
	            
	            for(Element link : pics){
	            	String linkjpg = link.attr("src");
	            	daoThreadPool.submit(new Runnable() {
						@Override
						public void run() {
							crawlerDao.SavePics(linkjpg);
						}
					});
	            }
	            
	             
	            for (Element link : links) {
                  //得到<a>...</a>里面的网址  http://pic.yesky.com/307/81584807.shtml
                  final String linkHref = link.attr("href");
                  if(!linkHref.isEmpty()&&(linkHref.startsWith("http://pic.yesky.com")||linkHref.startsWith("http://yesky.com"))){
                	  daoThreadPool.submit(new Runnable() {
      					@Override
      					public void run() {
      						crawlerDao.insertNewPageInfo(linkHref);
      					}
      				}); 
                  }
                }
            }
        }catch (Exception e) {
            System.out.println("网址：" + file.getName() + "解析出错");
            e.printStackTrace();
        }finally {
        	afterGetAvalidUrl(infoBin);
        	file.delete();
        	System.out.println("网址：" + file.getName() + "解析完毕");
        }
    }
    
    
    private void controlThread(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					checkNewUrl();
					try {
						Thread.sleep(600);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
    
    
    private void checkNewUrl() {
    	try {
    		executor.execute(new Runnable() {
      		  @Override
      		  public void run() {
      			PageInfoBin infoBin=crawlerDao.queryOneNotCheckUrl();
      			if(infoBin!=null){
      				//System.out.println("开始连接测试"+infoBin.getAddress());
      				storeMap.put(infoBin.getSTORENUM(),infoBin);
          			Save_Html(infoBin);//保存到本地的网页地址
      			}
      		  }});
		}catch (Exception e) {
			//e.printStackTrace();
		}
    }
}