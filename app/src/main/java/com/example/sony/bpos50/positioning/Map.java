package com.example.sony.bpos50.positioning;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.example.sony.bpos50.gltools.Circle;


public class Map {
	public HashMap<String,Room> roomList;
	public String selected;
	private boolean blink;
	private int blinkcounter;
	public long last_updated=0;
	Vector<Float> navi=null;
	public Circle s;
	float location_x;
	float location_y;
	float[] distances = {22.2f,20,16,21.2f,12.2f,21.2f,16,20};

	public Map(){
		roomList = new HashMap<String,Room>();
		blink=false;
		s= new Circle();
		blinkcounter=0;
		selected = "room5";
	}

	public void addObject(String id, String type, Vector<Float> points){
		roomList.put(id, new Room(id,type,points));
		if(type.equals("navigation")){
			navi = points;
			location_x = points.elementAt(6);
			location_y = points.elementAt(7);
		}
	}


	public void setSelected(int minor){
		switch(minor){
		case 37558:
			selected="room223";
			break;
		case 40079:
			selected="corridor201";
			break;
		case 53767:
			selected="corridor202";
			break;
		case 30661:
			selected="room226";
			break;
		case 41655:
			selected="corridor203";
			break;
		case 23598:
			selected="room223";
			break;
		default:
			break;
		}
		selected = "room5";
	}
	public void set_location(int ref,int direction,int step_count,float step_length){
		float refx=0,refy = 0;
		float total_length  = step_count * step_length;
		total_length = total_length/10;
		direction = direction==1?1:-1;
		float cur_dist;
		if(direction == 1) {
			cur_dist = distances[ref - 1];
		}
		else{
			cur_dist = distances[(ref-2)<0?(ref-2+8):(ref-2)];
		}
		int cur_ref=ref;
		while(total_length>cur_dist){
			total_length -= cur_dist;
			ref += direction;
			ref = ref<1?(ref+8):ref;
			ref = ref>8?(ref-8):ref;
			if(direction == 1) {
				cur_dist = distances[ref - 1];
			}
			else{
				cur_dist = distances[(ref-2)<0?(ref-2+8):(ref-2)];
			}
		}
		switch(ref){
			case 1:
				if(direction == 1) {
					refx = navi.elementAt(6)+total_length;
					refy = navi.elementAt(7);
				}
				else{
					refx = navi.elementAt(6);
					refy = navi.elementAt(7)-total_length;
				}
				break;
			case 2:
				if(direction == 1) {
					refx = navi.elementAt(4);
					refy = navi.elementAt(5)-total_length;
				}
				else{
					refx = navi.elementAt(4)-total_length;
					refy = navi.elementAt(5);
				}
				break;
			case 3:
				if(direction==1) {
					refx = navi.elementAt(4);
					refy = navi.elementAt(5) - 16 - 4 - total_length;
				}
				else{
					refx = navi.elementAt(4);
					refy = navi.elementAt(5) - 16 - 4 + total_length;
				}
				break;
			case 4:
				if(direction==1) {
					refx = navi.elementAt(4);
					refy = navi.elementAt(5) - 32 - 4 - total_length;
				}
				else{
					refx = navi.elementAt(4);
					refy = navi.elementAt(5) - 32 - 4 + total_length;
				}
				break;
			case 5:
				if(direction==1) {
					refx = navi.elementAt(2)-total_length;
					refy = navi.elementAt(3);
				}
				else{
					refx = navi.elementAt(2);
					refy = navi.elementAt(3) + total_length;
				}
				break;
			case 6:
				if(direction==1) {
					refx = navi.elementAt(0);
					refy = navi.elementAt(1)+total_length;
				}
				else{
					refx = navi.elementAt(0)+total_length;
					refy = navi.elementAt(1);
				}
				break;
			case 7:
				if(direction==1) {
					refx = navi.elementAt(6);
					refy = navi.elementAt(7) - 32 - 4 + total_length;
				}
				else{
					refx = navi.elementAt(6);
					refy = navi.elementAt(7) - 32 - 4 - total_length;
				}
				break;
			case 8:
				if(direction==1) {
					refx = navi.elementAt(6);
					refy = navi.elementAt(7) - 16 - 4 + total_length;
				}
				else{
					refx = navi.elementAt(6);
					refy = navi.elementAt(7) - 16 - 4 - total_length;
				}
				break;
		}
		location_x = refx;
		location_y = refy;

	}

	public void draw(GL10 gl){
		for (HashMap.Entry<String,Room> entry : roomList.entrySet())
		{
			if(entry.getValue().type.equals("room") || entry.getValue().type.equals("table"))
				entry.getValue().draw(gl,0,0,0,false);
			if(entry.getValue().type.equals("table")){

				for(int i=0;i<entry.getValue().vertices.length;i=i+3){
					gl.glPushMatrix();
					gl.glTranslatef(entry.getValue().vertices[i],entry.getValue().vertices[i+1],entry.getValue().vertices[i+2]);
					s.draw(gl,1,1,0,true);
					gl.glPopMatrix();
				}

			}

			if(entry.getValue().type.equals("navigation"))
				entry.getValue().draw(gl,1,0,0,false);
			if(entry.getKey().equals(selected)){
				entry.getValue().draw(gl,1,0,0,blink);
				if(blinkcounter==30){
					blink = blink?false:true;
					blinkcounter=0;
				}
				blinkcounter++;
			}


		}
		gl.glPushMatrix();
		gl.glTranslatef(location_x,location_y,0);
		s.draw(gl,1,0,0,true);
		gl.glPopMatrix();
	}
}

