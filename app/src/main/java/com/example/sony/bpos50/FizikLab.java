package com.example.sony.bpos50;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class FizikLab extends AppCompatActivity {
    Button MainActivityButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fizik_lab);

        MainActivityButton = (Button)findViewById(R.id.main_activity);

        MainActivityButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intomain = new Intent(FizikLab.this, MainActivity.class);
                startActivity(intomain);
            }
        });
    }



}
