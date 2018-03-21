package com.example.sony.bpos50.gltools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
/*
 * A square drawn in 2 triangles (using TRIANGLE_STRIP).
 */
public class Rectangle extends Shape {

	// Constructor - Setup the vertex buffer
	public Rectangle() {
		vertices = new float[12];
	}

}