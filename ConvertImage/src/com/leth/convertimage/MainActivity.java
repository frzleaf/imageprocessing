package com.leth.convertimage;

import javax.crypto.spec.IvParameterSpec;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {
	
	// Original picture
	Bitmap origBM;
	Bitmap grayBM;
	ImageView image;
	
	static {
		System.loadLibrary("convertimage");
	}
	
	public native void convertImage(Bitmap bin, Bitmap bout);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		image = (ImageView) findViewById( R.id.image);
		origBM = BitmapFactory.decodeResource(this.getResources(), R.drawable.sample);
		
		if ( origBM != null )
			image.setImageBitmap(origBM);
		
		Button resetBtn = (Button) findViewById( R.id.reset);
		Button convertBtn = (Button) findViewById( R.id.convert);
		
		resetBtn.setOnClickListener(this);
		convertBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch ( v.getId() ){
		case R.id.convert:
			grayBM = Bitmap.createBitmap(origBM.getWidth(), origBM.getHeight(), Config.ALPHA_8);
			convertImage(origBM, grayBM);
			image.setImageBitmap(grayBM);
			break;
		case R.id.reset:
			image.setImageBitmap(origBM);
			break;
		}
	}
}
