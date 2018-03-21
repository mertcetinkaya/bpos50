package com.example.sony.bpos50.gltools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Circle extends Shape {

	private int sides=32;
	private float r=1.0f;
	float theta = 0;


	public Circle(){
		vertices = new float[sides*3];
		DrawLoop();
		ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = vertexByteBuffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
	}

	public void DrawLoop() {
		for (int i = 0; i < sides * 3; i += 3) {
			vertices[i + 0] = (float) Math.cos(theta)*r;
			vertices[i + 1] = (float) Math.sin(theta)*r;
			vertices[i + 2] = 0;
			theta += 2*Math.PI / sides;
		}
	}
}
