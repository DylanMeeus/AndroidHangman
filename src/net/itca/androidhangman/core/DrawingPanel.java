package net.itca.androidhangman.core;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class DrawingPanel extends SurfaceView
{

	int errors;
	
	public DrawingPanel(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	public void setErrors(int error)
	{
		errors = error;
	}

	public DrawingPanel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
	}

	public DrawingPanel(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
	/*
	 * (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 * if(error <= 1){...}
	 * if(error <= 2){...} 
	 * To fall through different levels of drawing.
	 */

	@Override
	public void onDraw(Canvas canvas)
	{
		int width = this.getWidth();
		int height = this.getHeight();
		this.setBackgroundColor(Color.rgb(255, 255, 255));
		Paint brush = new Paint();
		brush.setColor(Color.BLACK);
		
		if(errors > 0)
			canvas.drawRect(width/10, height/12, (width/10)+10, height-10, brush); // rect 1
		if(errors > 1)
			canvas.drawRect(width/10,height/12,width/2,(height/10)+10,brush); // rect 2
		if(errors > 2)
			canvas.drawRect(width/2,height/12,width/2+10, height/4,brush); // rect2 3
		if(errors > 3)
			canvas.drawCircle(width/2, (height/4+40), 50, brush); // head
		if(errors > 4)
			canvas.drawLine(width/2, height/4+90, width/2, height-height/4, brush); // body
		if(errors > 5)
			canvas.drawLine(width/2, height/4+110, width/3, height-height/3, brush); // arm 1
		if(errors > 6)
			canvas.drawLine(width/2, height/4+110, width/4*3, height-height/3, brush); // arm 2
		if(errors > 7)
			canvas.drawLine(width/2, height-height/4, width/3, height-height/5, brush); // leg 1
		if(errors > 8)
		{
			canvas.drawLine(width/2, height-height/4, width/4*3, height-height/5, brush); // leg 2
			Paint textBrush = new Paint();
			textBrush.setColor(Color.RED);
		}
	}

	

	@Override
	public boolean performClick()
	{
		super.performClick();
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			performClick();
		}
		return true;
	}
}
