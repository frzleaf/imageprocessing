package com.leth.cropimage;

import java.util.ArrayList;
import java.util.List;

import com.leth.cropimage.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class CropView extends View implements OnTouchListener {
	private Paint paint;
	public List<Point> points;
	int DIST = 2;
	boolean flgPathDraw = true;
	boolean bfirstpoint = false;
	Point mfirstpoint = null;
	Point mlastpoint = null;
	public Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
	Context mContext;
	
	TextView tv_pos;
	int pos[];
	boolean finish = false;

	public CropView(Context c, TextView tv_pos) {
		super(c);

		mContext = c;
		setFocusable(true);
		setFocusableInTouchMode(true);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		paint.setPathEffect(new DashPathEffect(new float[] { 10, 20 }, 0));
		paint.setStrokeWidth(5);
		paint.setColor(Color.WHITE);

		this.setOnTouchListener(this);
		points = new ArrayList<Point>();

		bfirstpoint = false;
		this.pos = pos;

		this.tv_pos = tv_pos;
	}

	
	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(bitmap, 0, 0, null);
		Path path = new Path();
		boolean first = true;

		for (int i = 0; i < points.size(); i += 2) {
			Point point = points.get(i);
			if (first) {
				first = false;
				path.moveTo(point.x, point.y);
			} else if (i < points.size() - 1) {
				Point next = points.get(i + 1);
				path.lineTo(point.x, point.y);
			} else {
				mlastpoint = points.get(i);
				path.lineTo(point.x, point.y);
			}
		}
		canvas.drawPath(path, paint);
	}

	public boolean onTouch(View view, MotionEvent event) {
		
		if ( finish )
			return false;
		
		Point point = new Point();
		point.x = (int) event.getX();
		point.y = (int) event.getY();

		String txt_pos = "Position (x,y) = (" + point.x + "," + point.y + ")";
		tv_pos.setText(txt_pos);

		if (flgPathDraw) {

			if (bfirstpoint) {

				if (comparepoint(mfirstpoint, point)) {
					// points.add(point);
					points.add(mfirstpoint);
					flgPathDraw = false;
//					showcropdialog();
					finishPathDraw();
				} else {
					points.add(point);
				}
			} else {
				points.add(point);
			}

			if (!(bfirstpoint)) {

				mfirstpoint = point;
				bfirstpoint = true;
			}
		}

		invalidate();
		Log.e("Hi  ==>", "Size: " + point.x + " " + point.y);

		if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.d("Action up *******~~~~~~~>>>>", "called");
			mlastpoint = point;
			if (flgPathDraw) {
				if (points.size() > 12) {
					if (!comparepoint(mfirstpoint, mlastpoint)) {
						flgPathDraw = false;
						points.add(mfirstpoint);
						finishPathDraw();
					}
				}
			}
		}
		return true;
	}

	private boolean comparepoint(Point first, Point current) {
		int left_range_x = (int) (current.x - 3);
		int left_range_y = (int) (current.y - 3);

		int right_range_x = (int) (current.x + 3);
		int right_range_y = (int) (current.y + 3);

		if ((left_range_x < first.x && first.x < right_range_x) && (left_range_y < first.y && first.y < right_range_y)) {
			if (points.size() < 10) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	private void finishPathDraw(){
		Log.i("CROP VIEW", "FINISH PATH DRAW");
		finish = true;
	}

}