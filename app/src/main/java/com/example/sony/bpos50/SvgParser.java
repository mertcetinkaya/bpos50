package com.example.sony.bpos50;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.example.sony.bpos50.positioning.Map;

public class SvgParser {


	public static void Parse(Map map,InputStream in) throws XmlPullParserException, IOException{
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(in, null);
		Log.w("LOG","Starting to parse");
		int count=1;
		int event;
		try {
			event = parser.getEventType();

			while (event != XmlPullParser.END_DOCUMENT) {
				String name=parser.getName();

				switch (event){
				case XmlPullParser.START_TAG:
					break;
				case XmlPullParser.TEXT:
					break;
				case XmlPullParser.END_TAG:
					if(name.equals("polygon")){
						ParsePolygon(parser,map);
					}
					if(name.equals("path")){
						Log.e("SVG PARSER","path comes in");
						ParsePath(parser,map,count);
						count++;
					}
					break;
				}
				event = parser.next();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void ParsePolygon(XmlPullParser parser,Map map){
		String id = parser.getAttributeValue(null,"id");
		String type = parser.getAttributeValue(null,"type");
		String points = parser.getAttributeValue(null,"points");
		Log.w("SVG",id+ "  "+ type +" " + points);
		map.addObject(id, type, ParsePoints(points));
	}

	public static void ParsePath(XmlPullParser parser,Map map,int count){
		String id = parser.getAttributeValue(null,"id");
		String type = parser.getAttributeValue(null,"type");
		String path = parser.getAttributeValue(null,"d");
		Log.w("SVG",id+ "  "+ type +" " + path);
		Vector<Float> res = new Vector<Float>();
		String point_list[] = path.split(" ");
		float x=0,y=0;
		for(int i=1; i<point_list.length-1; i++){
			if(point_list[i].equals("c")){
				continue;
			}
			else {
				String tmp[] = point_list[i].split(",");
				x = Float.parseFloat(tmp[0]);
				y = Float.parseFloat(tmp[1]);
				res.add(x);
				res.add(y);
			}
		}
		Log.e("SVG PARSER",id+" "+type);
		if(id==null) {
			id = "obj" + count;
		}
		if(type==null) {
			type = "room";
		}
		map.addObject(id, type,res);
	}

	public static Vector<Float> ParsePoints(String points){
		Vector<Float> res = new Vector<Float>();
		String point_list[] = points.split(" ");
		for(int i=0; i<point_list.length; i++){
			String tmp[] = point_list[i].split(",");
			res.add(Float.parseFloat(tmp[0]));
			res.add(Float.parseFloat(tmp[1]));
		}
		return res;
	}


}
