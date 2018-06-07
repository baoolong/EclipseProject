package com.test.natives;

public class FFmpegNative {
	static{  
        System.loadLibrary("avutil-55");  
        System.loadLibrary("avcodec-57");  
        System.loadLibrary("swresample-2");  
        System.loadLibrary("avformat-57");  
        System.loadLibrary("swscale-4");  
        System.loadLibrary("avfilter-6"); 
        System.loadLibrary("avdevice-57");
        System.loadLibrary("postproc-54");
        System.loadLibrary("encodeH264");
        System.loadLibrary("encodeAac");
        System.loadLibrary("muxerToMP4"); 
//        System.loadLibrary("cmdOrder");
	}  
	private int mNativeContext = 0;
	
	public native int getVersion();
	public native int EncodingH264(byte[] yuvdata);
	public native void CloseVideo();
	
	
	public native int aacInit();
	public native int EncodingAAC(byte[] pcmdata,int length);
	public native void CloseAudio();
	
	public native int avcodec_find_decoder(int codecID);
	public native String avcodecinfo();
	public native int Composite264Aac(String h264filepath,String aacFilepath,String outfilepath,String angle);
	
	/**
	 * @return int 表示初始化是否成功
	 */
	public native int init(String destUrl,int w,int h);
	public native int push(byte[] bytes, int w, int h);
	public native int stop();
}
