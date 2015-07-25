package com.leth.cropimage;

import java.util.List;

import com.leth.cropimage.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.DisplayMetrics;
import android.widget.ImageView;


public class CropLib {
	
	public static Bitmap xferCrop (Bitmap bm, List<Point> pos_matrix ){
		
		Bitmap rs_bm = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
		Canvas canvas = new Canvas(rs_bm);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Path path = new Path();
		
		for ( Point p : pos_matrix)
			path.lineTo(p.x, p.y);
		
		canvas.drawPath(path, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bm, 0, 0, paint);
		
		return rs_bm;
	}
	
	public static native void  nativeCrop (Bitmap orig, Bitmap result, int []xarray, int []yarray , int length);
	
	static {
		System.loadLibrary("cropimage");
	}

}