package com.leth.convertimage;

import javax.crypto.spec.IvParameterSpec;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	// Original picture
	Bitmap origBM;
	Bitmap grayBM;
	ImageView image;
	TextView tv;

	static {
		System.loadLibrary("convertimage");
	}

	public native void convertImage(Bitmap bin, Bitmap bout);

	public Bitmap convertImageDirect(Bitmap bin) {

		int a, r, g, b, p;

		int w, h;
		w = bin.getWidth();
		h = bin.getHeight();

		Bitmap rs = Bitmap.createBitmap(w, h, Config.ARGB_8888);

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				p = bin.getPixel(i, j);
				a = Color.alpha(p);

				r = Color.red(p);
				g = Color.green(p);
				b = Color.blue(p);

				int m = Math.round((r + g + b) / 3);

				rs.setPixel(i, j, Color.argb(a, m, m, m));
			}
		}
		return rs;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		image = (ImageView) findViewById(R.id.image);
		origBM = BitmapFactory.decodeResource(this.getResources(), R.drawable.sample );

		if (origBM != null)
			image.setImageBitmap(origBM);

		Button resetBtn = (Button) findViewById(R.id.reset);
		Button ncBtn = (Button) findViewById(R.id.nativeconvert);
		Button dcBtn = (Button) findViewById(R.id.directconvert);

		resetBtn.setOnClickListener(this);
		ncBtn.setOnClickListener(this);
		dcBtn.setOnClickListener(this);

		tv = (TextView) findViewById(R.id.tv_rs);

	}

	@Override
	public void onClick(final View v) {

		if (v.getId() == R.id.nativeconvert || v.getId() == R.id.directconvert) {
			final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Converting...", true);
			
			new AsyncTask<Void, Void, String>() {
				
				protected String doInBackground(Void... params) {
					long t = System.currentTimeMillis();
					String convert_type = null;
					
					// Switch buttonId
					switch ( v.getId() ) {
					
					// Native convert
					case R.id.nativeconvert:
						//Set type
						convert_type = "Native";
						grayBM = Bitmap.createBitmap(origBM.getWidth(), origBM.getHeight(), Config.ARGB_8888);
						// Convert image by native method
						convertImage(origBM, grayBM);
						break;
						
					case R.id.directconvert:
						// Log
						Log.i("Convert Directly", "Start convert");
						// Set type name
						convert_type = "Java";
						// Convert image by java method
						grayBM = convertImageDirect(origBM);
						Log.i("Convert Directly", "Finish convert");
						break;
					}
					// Return string result to display on textview
					return String.format("%s convert in: %d miliseconds", 
											convert_type, System.currentTimeMillis() - t);
				}
				
				protected void onPostExecute(String result) {
					
					dialog.dismiss(); 					// Clear dialog
					image.setImageBitmap(grayBM);		// Set the image
					tv.setText(result);					// Set the text result
				}
				
			}.execute();
		}

		if (v.getId() == R.id.reset)
			image.setImageBitmap(origBM);
	}
}
