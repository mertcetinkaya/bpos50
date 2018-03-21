package com.example.sony.bpos50.gltools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Shape {
	public FloatBuffer vertexBuffer;
	public float vertices[];

	public Shape(){

	}
	
	
	public void buffer() {
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder()); 
		vertexBuffer = vbb.asFloatBuffer(); 
		vertexBuffer.put(vertices);      
		vertexBuffer.position(0);       
	}
	
	public void draw (GL10 gl,float r,float g,float b,boolean filled){
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glColor4f(r,g,b,1);
		if(filled)
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vertices.length / 3);
		else
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, (vertices.length / 3));
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
