package com.example.sony.bpos50.gltools;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.sony.bpos50.MainActivity;

import android.content.Context;
import android.opengl.GLSurfaceView;



public class GLRenderer implements GLSurfaceView.Renderer {

	float angleX = 0;   // (NEW)
	float angleY = 0;   // (NEW)
	float speedX = 0;   // (NEW)
	float speedY = 0;   // (NEW)
	float z = 1.0f;    // (NEW)
	float L=50;
	float aspect;
	Context context=null;



	long endTime;
	long startTime;
	long dt;
	boolean filled;

	public GLRenderer(Context context) {
		this.context=context;
		startTime=0;
		filled=false;
	}

	// Call back when the surface is first created or re-created.
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);  // Set color's clear-value to black
		gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
		gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
		gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
		gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
		gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance

		// You OpenGL|ES initialization code here
		// ......
	}

	// Call back after onSurfaceCreated() or whenever the window's size changes
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (height == 0) height = 1;   // To prevent divide by zero
		aspect = (float)width / height;

		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matches viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
		gl.glLoadIdentity();                 // Reset projection matrix
		// Use perspective projection
		gl.glOrthof(-L/2*aspect, L*aspect, -L/2, L, 0, 10);

		gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
		gl.glLoadIdentity();                 // Reset

		// You OpenGL|ES display re-sizing code here
		// ......
	}

	// Call back to draw the current frame.
	@Override
	public void onDrawFrame(GL10 gl) {
		// Clear color and depth buffers using clear-value set earlier
		endTime = System.currentTimeMillis();
		dt = endTime - startTime;
		if (dt < 33){
			try {
				Thread.sleep(33 - dt);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		startTime = System.currentTimeMillis();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();
		gl.glScalef(-1*z, z, z);
		gl.glTranslatef(speedX,-1*speedY,0);
		gl.glRotatef(180f,0f,0f,1f);
		gl.glLineWidth(3);
		((MainActivity)context).map.draw(gl);
	}
}