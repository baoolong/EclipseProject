package com.test.HelloWorld;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.example.helloworld.R;
import com.test.natives.FFmpegNative;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * 从摄像头采集图像，然后将图片用socket发送给对方显示
 * @author Mrdong
 *
 */
@SuppressWarnings("deprecation")
public class VideoCallActivity extends Activity implements SurfaceHolder.Callback{
	
	private Button button;
	private Camera mCamera;// Camera对象
	private SurfaceView mSurfaceView;// 显示摄像头获取的图像(预览)的surfaceView
	private SurfaceView mSurfaceView_show;// 显示接收到的图像的surfaceView
	private SurfaceHolder holder;// SurfaceView的控制器
	private FFmpegNative fFmpegLib;
	
    private AudioRecord audioRecord;
 // 音频获取源  
    private int audioSource = MediaRecorder.AudioSource.MIC;  
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025 
    //96000, 88200, 64000, 48000, 44100, 32000,24000, 22050, 16000, 12000, 11025, 8000, 7350
    private static int sampleRateInHz = 44100;  
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_IN_MONO为单声道  
    private static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;  
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。  
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;  
    // 缓冲区字节大小  
    private int bufferSizeInBytes = 0;
    private boolean isRecord = false;// 设置正在录制的状态  
    /**44100Hz编码 AAC时每次读的Byte大小为 4096，这个数据是FFMPEG计算打印出来的，具体看C代码av_samples_get_buffer_size*/
    private int pcmReadBufferSize=4096;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fFmpegLib=new  FFmpegNative();
		if (checkCameraHardware(this)) 
            Log.e("checkCameraHardware", "摄像头存在");// 验证摄像头是否存在
        /* 隐藏状态栏 */
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /* 隐藏标题栏 */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.another);
        init();
	}
	
	
	
	private void init(){
		fFmpegLib.init("", 480, 640);//初始化H264编码环境
		mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
		holder = mSurfaceView.getHolder();
		holder.addCallback(this);
		button=(Button)findViewById(R.id.button);
		//button.setText(fFmpegLib.avcodecinfo());//查看编解码器支持的类型
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}
//	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initCamera();
		startRecord();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stopCamera();
		stopRecord();
	}
	
    // 检测摄像头是否存在的私有方法
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;// 摄像头存在
        } else {
            return false;// 摄像头不存在
        }
    }
    
    
    /* 相机初始化的method */
    private void initCamera() {
    	try { mCamera = null;
            try {
                mCamera = Camera.open(0);
                //打开相机；在低版本里，只有open（）方法；高级版本加入此方法的意义是具有打开多个摄像机的能力，其中输入参数为摄像机的编号
                //在manifest中设定的最小版本会影响这里方法的调用，如果最小版本设定有误（版本过低），在ide里将不允许调用有参的open方法;
                //如果模拟器版本较高的话，无参的open方法将会获得null值!所以尽量使用通用版本的模拟器和API；
            } catch (Exception e) {
            	e.printStackTrace();
                Log.e("camera", "摄像头被占用");
            }
            if (mCamera == null) {
                Log.e("camera", "摄像机为空");
                System.exit(0);
            }
            mCamera.setPreviewDisplay(holder);//设置显示面板控制器
            mCamera.setDisplayOrientation(90);
            priviewCallBack pre = new priviewCallBack();//建立预览回调对象
            mCamera.setPreviewCallback(pre); //设置预览回调对象
            Camera.Parameters parameters = mCamera.getParameters();
            /* 设定相片大小为1024*768， 格式为JPG*/
            // parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPreviewSize(640, 480);
            parameters.setPreviewFormat(ImageFormat.YV12);
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPreviewFpsRange(20, 30); // 每秒显示25~25帧
            mCamera.setParameters(parameters);
            //mCamera.getParameters().setPreviewFormat(ImageFormat.JPEG);
            mCamera.startPreview();//开始预览，这步操作很重要
        } catch (Exception exception) {
            mCamera.release();
            mCamera = null;
        }
    }
    
    
    
    
    
    /* 停止相机的method */
    private void stopCamera() {
        if (mCamera != null) {
            try {
                /* 停止预览 */
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    Handler mHandler = new Handler() {  
        public void handleMessage(android.os.Message msg) {  
            switch (msg.what) {  
            case 1:  
                byte[] bytedata = msg.getData().getByteArray("messageyuvdata");  
                if (bytedata != null) {  
                    addVideoData(bytedata);  
                }  
                break;
            }
        };  
    }; 
    
    
    
 // 每次cam采集到新图像时调用的回调方法，前提是必须开启预览
    class priviewCallBack implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(final byte[] data, Camera camera) {
            Message msg = new Message();  
            Bundle bl = new Bundle();  
            bl.putByteArray("messageyuvdata", data);  
            msg.setData(bl);  
            msg.what = 1;  
            mHandler.sendMessage(msg);
        }
    }
    
    public synchronized void addVideoData(byte[] data) {  
        fFmpegLib.push(data, 480, 640);  
    }  
    
/************************************************************************************/
    
	
	/**
	 * 初始化 并开始录制   音频
	 */
	private void startRecord() { 
		fFmpegLib.aacInit();
		// 获得缓冲区字节大小  大小约1400   加上头文件信息 不超过1460
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,  
                channelConfig, audioFormat); //7168
        // 创建AudioRecord对象  
        audioRecord = new AudioRecord(audioSource, sampleRateInHz,  
                channelConfig, audioFormat, bufferSizeInBytes);
        
        audioRecord.startRecording();  
        // 让录制状态为true  
        isRecord = true;  
        // 开启音频文件写入线程  
        new Thread(new AudioRecordThread()).start();  
    } 
	
	
	 class AudioRecordThread implements Runnable {  
		 @Override  
		 public void run() {  
            try {//da duan  xiao  chang   xianzai duan
            	//new一个byte数组用来存一些字节数据，大小为缓冲区大小  
            	byte[] mData=new byte[pcmReadBufferSize];
            	startTime=System.currentTimeMillis();
    	        while (isRecord == true) {  
    	        	int readSize=audioRecord.read(mData,0,pcmReadBufferSize);
    	        	fFmpegLib.EncodingAAC(mData, readSize);
    	        } 
			} catch (Exception e) {
				e.printStackTrace();
			} 
		 }  
	 }
	 
	
	/**
	 * 停止  音频   录制
	 */
	 private void stopRecord() {  
		 if (audioRecord != null) {  
            System.out.println("stopRecord");  
            isRecord = false;//停止文件写入  
            audioRecord.stop();  
            audioRecord.release();//释放资源  
            audioRecord = null;  
		 }   
	 } 
    long startTime;
	 
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	stopCamera();
    	stopRecord();
    	System.out.println(System.currentTimeMillis()-startTime+"");
    	fFmpegLib.CloseAudio();
		fFmpegLib.stop();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	stopCamera();
    	stopRecord();
    	System.out.println(System.currentTimeMillis()-startTime+"");//13564
    	fFmpegLib.CloseAudio();
        fFmpegLib.stop();
    }
}
