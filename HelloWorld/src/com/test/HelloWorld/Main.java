package com.test.HelloWorld;


import com.example.helloworld.R;
import com.lgyw.emergency.natives.AntiDebugNative;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

public class Main extends Activity implements android.view.View.OnClickListener{
	
	private Button videoCall,muxer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*要显示的控件放在括号里*/
		setContentView(R.layout.mainlayout);
		initView();
		System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
	}


	private void initView() {
		videoCall=(Button) findViewById(R.id.VideoCall);
		muxer=(Button) findViewById(R.id.muxerMP4);
		videoCall.setOnClickListener(this);
		muxer.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.VideoCall:
			intent=new Intent(this,VideoCallActivity.class);
			startActivity(intent);
			break;
		case R.id.muxerMP4:
			intent=new Intent(this,MuxerMpegVideo.class);
			startActivity(intent);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println(requestCode);
		System.out.println(resultCode);
	}
}
