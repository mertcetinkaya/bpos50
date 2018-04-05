package com.example.sony.bpos50;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.*;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class ListRoute extends AppCompatActivity {
    Button MainActivityButton;
    Button SendRouteButton;
    EditText WriteRouteText;
    Button ShowRouteButton;
    Button SaveList;
    ToggleButton Wafer;
    ToggleButton Biscuit;
    ToggleButton Water;
    ToggleButton Cola;
    ToggleButton Rice;
    ToggleButton Bulgur;
    List<String> ShoppingList = new ArrayList<String>(6);
    List<String> ChocolateList = new ArrayList<String>(2);
    List<String> BeverageList = new ArrayList<String>(2);
    List<String> LegumesList = new ArrayList<String>(2);
    List<String> Route = new ArrayList<String>(3);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_route);

        MainActivityButton = (Button)findViewById(R.id.main_activity);
        SendRouteButton = (Button)findViewById(R.id.sendroutebutton);
        WriteRouteText = (EditText) findViewById(R.id.writeroutedittext);
        ShowRouteButton = (Button)findViewById(R.id.showroutebutton);
        SaveList = (Button)findViewById(R.id.savelistbutton);
        Wafer = (ToggleButton)findViewById(R.id.wafer);
        Biscuit = (ToggleButton)findViewById(R.id.biscuit);
        Water = (ToggleButton)findViewById(R.id.water);
        Cola = (ToggleButton)findViewById(R.id.cola);
        Rice = (ToggleButton)findViewById(R.id.rice);
        Bulgur = (ToggleButton)findViewById(R.id.bulgur);
        ChocolateList.add("Wafer");
        ChocolateList.add("Biscuit");
        BeverageList.add("Water");
        BeverageList.add("Cola");
        LegumesList.add("Bulgur");
        LegumesList.add("Rice");

        MainActivityButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intomain = new Intent(ListRoute.this, MainActivity.class);
                startActivity(intomain);
            }
        });

        SaveList.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                ShoppingList.clear();
                if(Wafer.isChecked())
                    ShoppingList.add("Wafer");
                if(Biscuit.isChecked())
                    ShoppingList.add("Biscuit");
                if(Water.isChecked())
                    ShoppingList.add("Water");
                if(Cola.isChecked())
                    ShoppingList.add("Cola");
                if(Rice.isChecked())
                    ShoppingList.add("Rice");
                if(Bulgur.isChecked())
                    ShoppingList.add("Bulgur");
            }
        });

        ShowRouteButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                WriteRouteText.setText("");
                Route.clear();
                if(!Collections.disjoint(ShoppingList,ChocolateList))
                    Route.add("Chocolate");
                if(!Collections.disjoint(ShoppingList,BeverageList))
                    Route.add("Beverage");
                if(!Collections.disjoint(ShoppingList,LegumesList))
                    Route.add("Legumes");
                for(int i=0;i<Route.size();i++)
                {
                    WriteRouteText.append(""+Route.get(i));
                    if(i<(Route.size()-1))
                        WriteRouteText.append(", ");
                }
            }
        });

    }






}