package com.example.sony.bpos50.gltools;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;


public class MapSurfaceView extends GLSurfaceView {
	GLRenderer renderer;    // Custom GL Renderer
	int pointer=1;
	double distance = 0;
	// For touch event
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320.0f;
	private float previousX;
	private float previousY;
	private float minZ=0.2f;

	// Constructor - Allocate and set the renderer
	public MapSurfaceView(Context context, AttributeSet attrs) {
		super(context);
		renderer = new GLRenderer(context);
		this.setRenderer(renderer);
		// Request focus, otherwise key/button won't react
		this.requestFocus();  
		this.setFocusableInTouchMode(true);
	}

	// Handler for key event
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent evt) {
		switch(keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:   // Decrease Y-rotational speed
			renderer.speedY -= 0.1f;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:  // Increase Y-rotational speed
			renderer.speedY += 0.1f;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:     // Decrease X-rotational speed
			renderer.speedX -= 0.1f;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:   // Increase X-rotational speed 
			renderer.speedX += 0.1f;
			break;
		case KeyEvent.KEYCODE_A:           // Zoom out (decrease z)
			renderer.z -= 0.2f;
			break;
		case KeyEvent.KEYCODE_Z:           // Zoom in (increase z)
			renderer.z += 0.2f;
			break;
		}
		return true;  // Event handled
	}

	// Handler for touch event
	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouchEvent(final MotionEvent evt) {
		float currentX = evt.getX();
		float currentY = evt.getY();
		float deltaX, deltaY;


		switch (evt.getAction()) {
		case MotionEvent.ACTION_DOWN:
			previousX = currentX;
			previousY = currentY;
			pointer=1;
			return true;
		case MotionEvent.ACTION_POINTER_2_DOWN:
			pointer=2;
			distance = fingerDist(evt);
			return true;
		case MotionEvent.ACTION_MOVE:
			// Modify rotational angles according to movement
			if( pointer == 2 ){
				if(evt.getPointerCount()>1){
					float newDist = fingerDist(evt);
					float d = (float) (distance/newDist);
					Log.i("Renderer","D :" + d);
					renderer.z += 1 - d;
					if(renderer.z<minZ) 
						renderer.z=minZ;
					distance = newDist;
				}
			}
			else{
				deltaX = currentX - previousX;
				deltaY = currentY - previousY;
				renderer.speedX-=deltaX/30*renderer.z;
				renderer.speedY+=deltaY/30*renderer.z;
				Log.i("Renderer","Deltax: " + deltaX +" Deltay: "+deltaY);
				previousX = currentX;
				previousY = currentY;
			}
		}
		// Save current x, y
		return true;  // Event handled
	}

	protected final float fingerDist(MotionEvent event){
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}
}