package com.example.sony.bpos50;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import android.os.Environment;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.Object;
import java.lang.Integer;
import java.text.DecimalFormat;
import java.lang.Math;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.widget.Button;
import android.widget.TextView;

import android.view.View.OnClickListener;



public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    private static final int REQUEST_ENABLE_BT = 1234;

    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 123456;
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";


    private BluetoothAdapter mBluetoothAdapter;
    boolean bluetooth;

    private SensorManager mSensorManager;
    private boolean mScanning;
    private Handler mHandler;
    public EditText myEdit;
    List<String> list_device_address = new ArrayList<String>();
    List<Integer> list_rssi = new ArrayList<Integer>();
    List<String> list_device_address_first = new ArrayList<String>();
    List<String> list_device_address_second = new ArrayList<String>();
    List<String> list_device_address_far = new ArrayList<String>();
    List<String> list_device_address_closest = new ArrayList<String>();
    List<String> list_device_address_all = Arrays.asList("C5:EC:3D:11:FB:31","C9:00:6A:7D:EF:B8",
            "CB:7F:3D:BD:0D:26","D3:8A:A2:B7:55:F7","E0:BE:D2:07:A4:25","E6:89:33:0C:97:FB","F9:12:3C:FE:46:96","FD:15:89:12:5C:2E");
    List<String> list_device_address_all_number = Arrays.asList("B1","B2","B3","B4","B5","B6","B7","B8");





    ToggleButton logbut;
    EditText logname;
    boolean is_pressed=false;
    String tag;
    TextView TvSteps;
    Button BtnStart;
    Button BtnStop;
    boolean step_watched=false;



    //List<String> list_device_address_to_save = new ArrayList<String>(Arrays.asList ( "C9:00:6A:7D:EF:B8" , "C5:EC:3D:11:FB:31" , "CB:7F:3D:BD:0D:26", "FD:15:89:12:5C:2E" ));
    List<String> list_to_write = new ArrayList<String>();



    private static final long SCAN_PERIOD = 1000;
    private static final long RESTING_PERIOD = 2000;


    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Steps Num: ";
    private int numSteps=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = (TextView) findViewById(R.id.tv_steps);
        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);

        BtnStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                step_watched=true;
                numSteps = 0;
                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);

            }
        });


        BtnStop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                step_watched=false;
                sensorManager.unregisterListener(MainActivity.this);

            }
        });





        myEdit=(EditText)findViewById(R.id.editText);
        logname=(EditText)findViewById(R.id.textView);
        logbut=(ToggleButton) findViewById(R.id.toggleButton);
        logbut.setChecked(false);
        logbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_pressed = logbut.isChecked();
                if(is_pressed) {
                    tag = logname.getText().toString();
                }
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        AssetManager mngr = getAssets();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mHandler = new Handler();
        scanLeDevice(true);




    }

    //public DecimalFormat df2 = new DecimalFormat(".##");
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    double x=0;
    double y=0;
    double z=0;
    List<Double> gyro_z_list = new ArrayList<Double>();
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //Log.e("GYRO",event.values[0]+" "+event.values[1]+" "+event.values[2]);

            if(gyro_z_list.size()==30){
                gyro_z_list.clear();
            }


            x=event.values[0];
            y=event.values[1];
            z=event.values[2];
            gyro_z_list.add(z);



            //TvSteps.setText(/*TEXT_NUM_STEPS + numSteps +", ref: " + reference_device_number + ", dir: " +direction+ */" gyro: \n"
                    //+ x + " \n "+ y + " \n " + z + "\n hypo: " + Math.sqrt(x*x+y*y+z*z));

/*
            if(is_pressed){
                clearList(list_to_write);
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                list_to_write.add(ts);
                list_to_write.add(",");
                list_to_write.add(""+z);
                list_to_write.add("\n");
                String last_list_to_write = "";
                for (String x : list_to_write)
                {
                    last_list_to_write += x + "";
                }


                writeFile(last_list_to_write,"notes.csv");
            }
*/




        }
    }

    @Override

    public void step(long timeNs) {
        numSteps++;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps +", ref: " + reference_device_number + ", dir: " +direction);


    }


    private void writeFile(String data,String fileName) {

        File extStore = Environment.getExternalStorageDirectory();
        // ==> /storage/emulated/0/note.txt
        String path =  "/sdcard/" + fileName;
        Log.i("ExternalStorageDemo", "Save to: " + path);

        try {
            File myFile = new File(path);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();

            //Toast.makeText(getApplicationContext(), fileName + " saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("fileWriter", e.getMessage());
        }
    }

    private void clearList(List list){
        list.clear();
    }
    int count;
    public int direction;
    int present_reference_index;
    int previous_reference_index;
    public String reference_device;
    public String reference_device_number;
    public String previous_reference;
    public String present_reference;
    public boolean first_reference=true;
    int previous_rssi=-100;
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            myEdit.setText("");
            clearList(list_device_address);
            clearList(list_rssi);
            clearList(list_to_write);
            count=0;
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    myEdit.append("\n   Total beacon count: " + count);
                    myEdit.append("\n"+list_device_address+"\n");

                    if(Collections.max(list_rssi)>=-80){
                        int max_rssi=Collections.max(list_rssi);
                        int max_rssi_index=list_rssi.indexOf(max_rssi);
                        reference_device=list_device_address.get(max_rssi_index);
                        reference_device_number=list_device_address_all_number.get(list_device_address_all.indexOf(reference_device));
                        present_reference=reference_device_number;
                        present_reference_index=list_device_address_all_number.indexOf(present_reference);

/*
                        if(list_rssi.get(list_device_address.indexOf(list_device_address_all.get(present_reference_index))) >
                                list_rssi.get(list_device_address.indexOf(list_device_address_all.get(previous_reference_index))) && present_reference_index%8 >
                                previous_reference_index%8)
                            direction=1;
                        else if(list_rssi.get(list_device_address.indexOf(list_device_address_all.get(present_reference_index))) >
                                list_rssi.get(list_device_address.indexOf(list_device_address_all.get(previous_reference_index))) && present_reference_index%8 <
                                previous_reference_index%8)
                            direction=2;
                        else if(list_rssi.get(list_device_address.indexOf(list_device_address_all.get(present_reference_index))) <
                                list_rssi.get(list_device_address.indexOf(list_device_address_all.get(previous_reference_index))) && present_reference_index%8 <
                                previous_reference_index%8)
                            direction=2;
                        else if(list_rssi.get(list_device_address.indexOf(list_device_address_all.get(present_reference_index))) <
                                list_rssi.get(list_device_address.indexOf(list_device_address_all.get(previous_reference_index))) && present_reference_index%8 >
                                previous_reference_index%8)
                            direction=1;
                        else
                            direction=-1;
*/
                        double treshold=2;

                        int present_rssi=list_rssi.get(list_device_address.indexOf(list_device_address_all.get(present_reference_index)));

                        if(gyro_z_list.size()>0 && (Collections.max(gyro_z_list)>treshold || Collections.min(gyro_z_list)<-treshold) && direction==1){
                            direction=2;
                            numSteps=0;
                        }

                        else if(gyro_z_list.size()>0 && (Collections.max(gyro_z_list)>treshold || Collections.min(gyro_z_list)<-treshold) && direction==2){
                            direction=1;
                            numSteps=0;
                        }



                        else if((present_reference_index==7 && previous_reference_index==0) || (present_reference_index==7 && previous_reference_index==1) ){
                            direction=2;
                            numSteps=0;
                        }

                        else if((present_reference_index==0 && previous_reference_index==7) || (present_reference_index==1 && previous_reference_index==7)){
                            direction=1;
                            numSteps=0;
                        }

                        else{
                            if((previous_reference_index+1)%list_device_address_all_number.size()==present_reference_index
                                    || (previous_reference_index+2)%list_device_address_all_number.size()==present_reference_index){
                                direction=1;
                                numSteps=0;
                            }

                            else if((previous_reference_index-1)%list_device_address_all_number.size()==present_reference_index
                                    || (previous_reference_index-2)%list_device_address_all_number.size()==present_reference_index){
                                direction=2;
                                numSteps=0;
                            }

                            else{
                                if(present_reference_index==previous_reference_index && present_rssi>=previous_rssi && direction==1)
                                    direction=1;
                                else if(present_reference_index==previous_reference_index && present_rssi>=previous_rssi && direction==2)
                                    direction=2;

                                else{
                                    int a=present_reference_index-1;
                                    int b=present_reference_index;
                                    int c=present_reference_index+1;

                                    if(a==-1) a=7;
                                    if(c==8) c=0;

                                    if(list_device_address.contains(list_device_address_all.get(a)) && list_device_address.contains(list_device_address_all.get(b))
                                            && list_device_address.contains(list_device_address_all.get(c))){
                                        int a_rssi=list_rssi.get(list_device_address.indexOf(list_device_address_all.get(a)));
                                        int b_rssi=list_rssi.get(list_device_address.indexOf(list_device_address_all.get(b)));
                                        int c_rssi=list_rssi.get(list_device_address.indexOf(list_device_address_all.get(c)));

                                        if(c_rssi>a_rssi){
                                            if(direction==1)
                                                direction=1;
                                            else{
                                                if(gyro_z_list.size()>0 && (Collections.max(gyro_z_list)>treshold || Collections.min(gyro_z_list)<-treshold)){
                                                    direction=1;
                                                    numSteps=0;
                                                }


                                            }
                                        }
                                        else if(a_rssi>c_rssi){
                                            if(direction==2)
                                                direction=2;
                                            else{
                                                if(gyro_z_list.size()>0 && (Collections.max(gyro_z_list)>treshold || Collections.min(gyro_z_list)<-treshold)){
                                                    direction=2;
                                                    numSteps=0;
                                                }

                                            }
                                        }
                                    }

                                    else{

                                    direction=-1;
                                    numSteps=0;

                                    }
                                }

                            }

                        }


                        previous_reference_index=present_reference_index;
                        previous_rssi=list_rssi.get(list_device_address.indexOf(list_device_address_all.get(previous_reference_index)));
                    }



                    //if(is_pressed) {
                        /*
                        Long tsLong = System.currentTimeMillis();
                        String ts = tsLong.toString();
                        list_to_write.add(ts);
                        if(step_watched){
                            list_to_write.add(",");
                            list_to_write.add(""+numSteps);
                        }
                        if(tag.isEmpty()==false){
                            list_to_write.add(",");
                            list_to_write.add(tag);
                        }
                        if(count>0){
                            list_to_write.add(",");
                        }
                        if(count==0){
                            list_to_write.add("\n");
                        }

                        for (int i=0;i<count;i+=1){
                            list_to_write.add(list_device_address.get(i));
                            list_to_write.add(",");
                            list_to_write.add(list_rssi.get(i).toString());
                            if(i!=count-1)
                                list_to_write.add(",");
                            if(i==count-1)
                                list_to_write.add("\n");
                        }

                        String last_list_to_write = "";
                        for (String x : list_to_write)
                        {
                            last_list_to_write += x + "";
                        }


                        writeFile(last_list_to_write,"notes.csv");
                        */

                        Long tsLong = System.currentTimeMillis();
                        String ts = tsLong.toString();
                        list_to_write.add(ts);
                        list_to_write.add(",");
                        list_to_write.add(""+direction);
                        list_to_write.add(",");
                        list_to_write.add(""+numSteps);
                        list_to_write.add(",");
                        list_to_write.add(reference_device_number);
                        list_to_write.add("\n");
                        String last_list_to_write = "";
                        for (String x : list_to_write)
                        {
                            last_list_to_write += x + "";
                        }


                        writeFile(last_list_to_write,"notes.csv");



                    //}
                }
            }, SCAN_PERIOD);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanLeDevice(true);
                }
            }, RESTING_PERIOD);

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }




    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device,final int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ParcelUuid[] asdsa =  device.getUuids();
                            if(asdsa!=null) {
                                for (ParcelUuid uuid : asdsa) {
                                    Log.e("asdasda", "UUID: " + uuid.getUuid().toString());
                                }
                            }
                            String name = device.getName();
                            String address=device.getAddress();
                            if (name != null) {
                                if (list_device_address.contains(address) == false && name.contains("EST") &&
                                        (address.equals("C9:00:6A:7D:EF:B8") || address.equals("C5:EC:3D:11:FB:31") || address.equals("CB:7F:3D:BD:0D:26") || address.equals("FD:15:89:12:5C:2E")
                                                || address.equals("D3:8A:A2:B7:55:F7") || address.equals("E0:BE:D2:07:A4:25") || address.equals("F9:12:3C:FE:46:96") || address.equals("E6:89:33:0C:97:FB"))) {
                                    list_device_address.add(address);
                                    list_rssi.add(rssi);
                                    count+=1;
                                    myEdit.append(device.getName() + ", " +list_device_address_all_number.get(list_device_address_all.indexOf(address))+ ", " + rssi + ", count: " + count + "\n");
                                }

                            }

                        }
                    });
                }
            };




}