package com.leth.cropimage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	CropView cr_view;
	Bitmap bm_cropped;
	LinearLayout lo_img;
	TextView tv_rs;
	TextView tv_pos;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		lo_img = (LinearLayout) findViewById(R.id.lo_img);
		tv_pos = (TextView) findViewById(R.id.tv_pos);
		tv_rs = (TextView) findViewById(R.id.tv_rs);

		// Set image on the right position
		int pos[] = new int[2];
		// Replace the image by another
		cr_view = new CropView(this, tv_pos);

		lo_img.addView(cr_view);

		Button nbtn = (Button) findViewById(R.id.nativ_btn);
		Button xbtn = (Button) findViewById(R.id.xfer_btn);
		Button rbtn = (Button) findViewById(R.id.reset);
		nbtn.setOnClickListener(this);
		xbtn.setOnClickListener(this);
		rbtn.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nativ_btn:
			nativeClick(v);
			break;
		case R.id.xfer_btn:
			xferClick(v);
			break;
		case R.id.reset:
			reset();
		}
	}

	private void nativeClick(View v) {

		// Change the List<point> to 2 arrays of x and y which pass to native
		// method
		int[] xar = new int[cr_view.points.size()];
		int[] yar = new int[cr_view.points.size()];
		int i = 0;
		for (Point p : cr_view.points) {
			xar[i] = p.x;
			yar[i++] = p.y;
		}

		bm_cropped = Bitmap.createBitmap(cr_view.bitmap.getWidth(), cr_view.bitmap.getHeight(),
				cr_view.bitmap.getConfig());

		// Crop & Evaluate
		long t = 0;
		if (cr_view.points.size() > 0) {
			t = System.currentTimeMillis();
			CropLib.nativeCrop(cr_view.bitmap, bm_cropped, xar, yar, xar.length);
			t = System.currentTimeMillis() - t;
		}
		// Display the result
		tv_rs.setText(String.format("Crop using native method in: %d ms", t));
		lo_img.removeAllViews();
		ImageView iv = new ImageView(MainActivity.this);
		iv.setImageBitmap(bm_cropped);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(bm_cropped.getWidth(), bm_cropped.getHeight());
		params.leftMargin = 0;
		params.topMargin = 0;
		lo_img.addView(iv, params);
	}

	private void xferClick(View v) {

		// Crop the image & Evaluation
		long t = System.currentTimeMillis();
		bm_cropped = CropLib.xferCrop(cr_view.bitmap, cr_view.points);
		t = System.currentTimeMillis() - t;

		// Display the result
		lo_img.removeView(cr_view);
		tv_rs.setText(String.format("Crop using XferMode in: %d ms", t));
		ImageView iv = new ImageView(MainActivity.this);
		iv.setImageBitmap(bm_cropped);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(bm_cropped.getWidth(), bm_cropped.getHeight());
		params.leftMargin = 0;
		params.topMargin = 0;
		lo_img.addView(iv, params);

	}

	private void reset() {
		lo_img.removeAllViews();
		cr_view = new CropView(this, tv_pos);
		lo_img.addView(cr_view);
	}
}
