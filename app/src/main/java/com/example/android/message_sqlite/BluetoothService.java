package com.example.android.message_sqlite;
import android.app.Service;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.IBinder;
        import android.os.Message;
        import android.support.annotation.Nullable;
        import android.util.Log;
        import android.os.Handler;

        import org.json.JSONArray;

        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.Set;
        import java.util.UUID;
        import java.util.Calendar;

/**
 * Created by Devaraj on 5/16/2017.
 */

public class BluetoothService extends Service {

    int second,minute,hour,day,month,year;
    protected static final String handshake = "1";
    protected static final int MESSAGE_READ = 1;
    String address = null; //"98:D3:31:FD:5F:F0"; //20:16:12:28:07:62
    final static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    boolean paired = false;
    BluetoothDevice myDevice = null;
    BluetoothAdapter myAdapter;
    Set<BluetoothDevice> pairedDevices;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    myBtThread bt = new myBtThread();



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public void onCreate(){
        // Toast.makeText(getApplicationContext(),"Reading Sensor data", Toast.LENGTH_LONG).show();
        super.onCreate();
        Log.d("Bt_Service","Creating Service");
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        //Toast.makeText(getApplicationContext(),"Service Started",Toast.LENGTH_LONG).show();
        Log.d("Bt_Service","Starting Thread");
        queryPaired();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        bt.setRunning(false);
        //Log.d("Service","Destroying Thread");
        super.onDestroy();
    }

   /* Handler mHandler = new Handler() {
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (msg.equals(MESSAGE_READ))
            {
                byte[] readBuf = (byte[]) msg.obj;
                String strIncom = new String(readBuf, 0, 5);
                Log.d("strIncom", strIncom);
            }
        }

    };*/



    private void queryPaired(){
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = myAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                if (deviceName.equals("HC-06")) {
                    paired = true;
                    myDevice = device;
                    address = device.getAddress();
                    break;
                }
            }
            if (paired) {
                //Toast.makeText(this, "HR Monitor already paired", Toast.LENGTH_SHORT).show();
                Connectbt();
                //searchHR();
            }
            else {
                //Toast.makeText(this, "HR Monitor not paired. Pair device first", Toast.LENGTH_SHORT).show();
                //Intent btSettings = new Intent();
                //btSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                //startActivity(btSettings);
                //searchHR();
            }
        }
    }

    private void Connectbt(){
        BluetoothDevice device = myAdapter.getRemoteDevice(address);
        BluetoothSocket mySocket = null;
        try {
            mySocket = device.createRfcommSocketToServiceRecord(myUUID);
        }catch (IOException e){
            //Log.e("Connect", "Socket's create() method failed", e);
        }
        myAdapter.cancelDiscovery();
        try {
            mySocket.connect();
        }catch (IOException connectException){
            try {
                mySocket.close();
            } catch (IOException closeException) {
                //Log.e("Connect", "Could not close the client socket", closeException);
            }
        }
        //Log.d("Connect", "Connection Succeeded");
        try{
            outputStream = mySocket.getOutputStream();
        }catch (IOException e){
            // Log.e("Connect", "output stream creation failed");
        }
        try{
            inputStream = mySocket.getInputStream();
        }catch (IOException e){
            //Log.e("Connect", "input stream creation failed");
        }
//        sendData(handshake);
//        Calendar cal = Calendar.getInstance();//
//        second = cal.get(Calendar.SECOND);
//        minute = cal.get(Calendar.MINUTE);
//        hour = cal.get(Calendar.HOUR);
//        day = cal.get(Calendar.DAY_OF_MONTH);
//        month = cal.get(Calendar.MONTH);
//        year = cal.get(Calendar.YEAR);
//        String updateTime = ""+hour+minute+second+"";
//        String updateDate = ""+day+month+year+"";
//        sendData(updateTime);
//        sendData(updateDate);
        bt.start();
    }

    private class myBtThread extends Thread {
        boolean running;

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            //byte[] NewBuffer = new byte[100];
            byte[] buffer = new byte[100];
            int bytes;
            //Log.d("Thread","Getting Data");
            running = true;
            while (running){
                try{
                    sleep(30);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                try{
                    bytes = inputStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    String[] Data = readMessage.split(",");
                    for(String value:Data){
                        if (value.equals(""))
                        {
                            //do nothing
                        }
                        else{
                            float pluse = Float.valueOf(value);
                            Bundle Hrate = new Bundle();
                            Hrate.putFloat("pulse",pluse);
                            Intent HRintent = new Intent();
                            HRintent.setAction("HR_UPDATE");
                            HRintent.putExtras(Hrate);
                            sendBroadcast(HRintent);
                        }
                    }
                    //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    //byte[] readBuf = buffer;
                    //String Data = new String(buffer);
                    //Log.d("Bt_Service",readMessage);
                }catch (IOException e){
                    e.printStackTrace();
                    Log.e("Read", "errror when reading");
                }
            }
        }

    }
    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        //Log.d("sendData", "...Send data: " + message + "...");
        try {
            outputStream.write(msgBuffer);
        } catch (IOException e) {
            //Log.e("sendData", "Error sending data");
        }
    }
}
