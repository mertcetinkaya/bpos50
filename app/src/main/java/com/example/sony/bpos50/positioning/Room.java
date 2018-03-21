package com.example.sony.bpos50.positioning;

import java.util.Vector;

import com.example.sony.bpos50.gltools.Shape;


public class Room extends Shape{
	public String id;
	public String type;
	
	public Room(String id,String type,Vector<Float> vertix){
		this.id = id;
		this.type = type;
		vertices = new float[vertix.size()+vertix.size()/2];
		int j=0;
		for(int i=0;i<vertices.length;i+=3){
			vertices[i] = vertix.get(j);
			vertices[i+1] = vertix.get(j+1);
			vertices[i+2] = 0f;
			j+=2;
		}
		buffer();
	}

}
