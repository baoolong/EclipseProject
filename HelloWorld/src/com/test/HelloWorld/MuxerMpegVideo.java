package com.test.HelloWorld;

import com.test.natives.FFmpegNative;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

public class MuxerMpegVideo extends Activity{
	
	private FFmpegNative fFmpegLib;
	private final String filename_out = "/sdcard/yourname.h264";
	private final String aac_out_filename = "/sdcard/test.aac";
	private final String mp4Out="/sdcard/mp4file.mp4";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fFmpegLib=new  FFmpegNative();
		fFmpegLib.Composite264Aac(filename_out, aac_out_filename, mp4Out, "0");
	}
}
