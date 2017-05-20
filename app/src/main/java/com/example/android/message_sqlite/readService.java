package com.example.android.message_sqlite;


        import android.app.Service;
        import android.content.Intent;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.IBinder;
        import android.support.annotation.Nullable;
        import android.util.Log;
        import android.widget.Switch;
        import android.widget.Toast;

        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InterruptedIOException;
        import java.io.OutputStream;
        import java.util.Set;
        import java.util.UUID;
        import android.os.Handler;
        import java.util.logging.LogRecord;

//import java.io.InputStream;
//import java.io.OutputStream;

/**
 * Created by Devaraj on 5/16/2017.
 */


public class readService extends Service implements SensorEventListener {

    private SensorManager SM;
    myServiceThread acc = new myServiceThread();
    String Xacc,Yacc,Zacc;
    double Xaxis,Yaxis,Zaxis;

    final static String ACTION_UPDATE_ACCELEROMETER = "UPDATE_ACCELEROMETER";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public void onCreate(){
        // Toast.makeText(getApplicationContext(),"Reading Sensor data", Toast.LENGTH_LONG).show();
        super.onCreate();
        //Log.d("Service","Starting Service");
        Sensor accel;
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        accel = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Xaxis = event.values[0];
        Yaxis = event.values[1];
        Zaxis = event.values[2];
        Xacc = ("X axis: "+Xaxis+"m/s2");
        Yacc =("Y axis: "+Yaxis+"m/s2");
        Zacc =("Z axis: "+Zaxis+"m/s2");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not relevant
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        //Toast.makeText(getApplicationContext(),"Service Started",Toast.LENGTH_LONG).show();
        acc.start();
        //Log.d("Service","Starting Thread");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        acc.setRunning(false);
        //Log.d("Service","Destroying Thread");
        super.onDestroy();
    }

    private class myServiceThread extends Thread{
        boolean running;
        public void setRunning(boolean running){
            this.running = running;
        }
        @Override
        public void run(){
            running = true;
            while(running){
                Intent accIntent = new Intent();
                accIntent.setAction(ACTION_UPDATE_ACCELEROMETER);
                Bundle accelero = new Bundle();
                accelero.putDouble("Xacc", Xaxis);
                accelero.putDouble("Yacc", Yaxis);
                accelero.putDouble("Zacc", Zaxis);
                accelero.putString("Xaxis", Xacc);
                accelero.putString("Yaxis", Yacc);
                accelero.putString("Zaxis", Zacc);
                accIntent.putExtras(accelero);
                //Log.d("Gforce", Xacc + "," + Yacc + "," + Zacc);
                sendBroadcast(accIntent);

            }
        }
    }
}
