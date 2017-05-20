package com.example.android.message_sqlite;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
//import android.os.Handler;


//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
import java.util.Set;
//import java.util.UUID;


import static com.example.android.message_sqlite.MainActivity.KEY_USERNAME;
import static com.example.android.message_sqlite.R.id.lblLocation;
import static com.example.android.message_sqlite.R.id.textView;
import static com.example.android.message_sqlite.R.id.username;

public class LoginActivity extends AppCompatActivity {
    public static final String LOGIN_URL =  GlobalVariables.serverBaseURL + "/getClosestUser.php?username=";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_CONTACT = "user_contact";
    public static final String KEY_EMERGENCY_CONTACT = "emergency_contact";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String JSON_ARRAY = "result";
    private TextView textViewContact;
    private SharedPreferences sharedPreferences;
    private static final String MyPrefs = "TestPreferences";
    private Location mLastLocation;
    GPS_cordinates gps;
    CreateURL  interrupt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        Bundle intentBundle = intent.getExtras();
        String loggedUser = intentBundle.getString("KEY_USERNAME");
        loggedUser = capitalizeFirstCharacter(loggedUser);

        System.out.println("starting gps fetching");

        startService(new Intent(this, GPS_cordinates.class));
        Log.d("intent", "intent is being executed!");
        String message = intentBundle.getString("MESSAGE");

        TextView loginUsername = (TextView) findViewById(R.id.login_user);
        TextView successMessage = (TextView) findViewById(R.id.message);
        textViewContact = (TextView) findViewById(R.id.textView6);
        loginUsername.setText(loggedUser);
        successMessage.setText(message);
       //registering the broadcast receiver
        interrupt=new CreateURL();
        IntentFilter intentfilter=new IntentFilter();
        intentfilter.addAction("emergency");
        registerReceiver(interrupt,intentfilter);

        //Creating a button to show the demo of how message get sends on interrupt received
        Button emergency_message = (Button) findViewById(R.id.buttonSend);
        emergency_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //method to get nearby user username and contact number from the database
        getResponse();
            }
        });

    }


// Receiving the broadcast
    private class CreateURL extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        String create =intent.getAction();
        if(create.equals("emergency")){
            String emergency=intent.getStringExtra("HeartAttack");
            //checking if the value in the key HeartAttack is SOS
            if(emergency.equals("SOS")){
                // If yes then interrupt received find nearby user as help is needed
                getResponse();
            }
        }
    }
}

    // method to make first alphabet of username capital to print it in the welcom message
    private String capitalizeFirstCharacter(String textInput) {
        String input = textInput.toLowerCase();
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output;
    }

    //method to find nearby user, contains volley string request
    public void getResponse() {

        sharedPreferences = getSharedPreferences(MyPrefs, MODE_PRIVATE);
        final String user_result = sharedPreferences.getString("Susername", "default string");
        System.out.println("Resulted Value in shared pref on button click : " + user_result);
        final String username = user_result.trim();
        System.out.println("Resulted Value in shared pref : " + username);

        String url = LOGIN_URL + username;
        System.out.println("Resulted Value in urlf in message service : " + url);
        //StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Resulted Value in : " + response);
                //  String jsonResult = showJSON(response);
                System.out.println("jsonResult: " + response);
               showJSON(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_USERNAME, username);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);


    }

    // Method to extract the current user location and emergency contacts, nearby users contact and username
    public void showJSON(String response){
        User currentUser;
        String emergency_contact="";
        try {

            JSONObject jsonObject = new JSONObject(response);

            System.out.println("jason object : "  );

//            {"result":{"emergency_contact":"+16619036222","latitude":"39.046281","longitude":"-77.474723","closest_people":[{"username":"Sarosh","latitude":"39.046281","longitude":"-77.474723","user_contact":"+17035418471"},{"username":"Suraj","latitude":"39.046281","longitude":"-77.474723","user_contact":"+15715980793"}]}}
            if(jsonObject.has("result")){
                JSONObject resultObj = jsonObject.getJSONObject("result");
                currentUser = new User();
                sharedPreferences = getSharedPreferences(MyPrefs, MODE_PRIVATE);
                final String user_result = sharedPreferences.getString("Susername", "default string");
                final String username = user_result.trim();
                currentUser.username = username;
                currentUser.emergencyContact = resultObj.getString("emergency_contact");
                currentUser.latitude = resultObj.getString("latitude");
                currentUser.longitude = resultObj.getString("longitude");

                List<User> closestUsers = new ArrayList<>();
                JSONArray closest_people = resultObj.getJSONArray("closest_people");
                for(int i =0; i < closest_people.length(); i++){
                    JSONObject person = closest_people.getJSONObject(i);
                    User user = new User();
                    user.username = person.getString("username");
                    user.latitude = person.getString("latitude");
                    user.longitude = person.getString("longitude");
                    user.userContact = person.getString("user_contact");
                    closestUsers.add(user);
                }
               sendAlerts(currentUser, closestUsers);

            } else {
                System.out.println("Incorrect format of result received");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
       textViewContact.setText("Emergenvy contact:\t" +emergency_contact);
    }

// creting a standard class for user which has all the fields which are receiving from server
    public class User {
        public String username;
        public String latitude;
        public String longitude;
        public String userContact;
        public String emergencyContact;

        @Override
        public String toString(){
            return "{"+ this.username + " "+ this.latitude + " "+ this.longitude + " "+ this.userContact + " "+ this.emergencyContact +"}";
        }

    }



    public void sendAlerts(final User currentUser, final List<User> closertUsers)
    {


                String dlat = currentUser.latitude.toString();
                String dlng = currentUser.longitude.toString();
                String emergencycontact= currentUser.emergencyContact.toString();
                // creating the routing url by inserting the currentuser i.e. the patients location coordinates
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr="+dlat+","+dlng);

       // sending sms to emergency contact
        String sms_emer = "Hello emergency saviour, Help is needed at location:"+uri;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(emergencycontact, null, sms_emer , null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // getting the phone nearby users phone  number sending sms to them
                for(User user: closertUsers){
                    System.out.println("insidemap: ");
                    System.out.println(user.userContact.toString());

                    String phoneNo =  user.userContact.toString();
                    String username =  user.username.toString();

                    System.out.println(phoneNo);
                    System.out.println(username);
                    String sms = "Hi "+username+ " Help is needed at location:"+uri;
                    System.out.println(sms);
                   // sending the message using the sms as the smsbody and phoneno as contact number
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                        Toast.makeText(getApplicationContext(), "SMS Sent!",
                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "SMS failed, please try again later!",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }



                    Log.d("intent", " sos_service is being excecuted!");


                }
            }







//
// bluetooth part which is commented due to load on the main activity
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode,resultCode,data);
//        if(resultCode == RESULT_CANCELED){
//            Toast.makeText(this, "Bluetooth must be enabled to continue", Toast.LENGTH_SHORT).show();
//            turnOnBT();
//        }
//        else if(resultCode == RESULT_OK) {
//            Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
//            readIntent = new Intent (LoginActivity.this,readService.class);
//            startService(readIntent);
//            BtIntent = new Intent (LoginActivity.this,BluetoothService.class);
//            startService(BtIntent);
//        }
//    }
//
//    private void turnOnBT(){
//        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(intent,Request_BT_Enable);
//    }
//
//    @Override
//    protected void onStart(){
//        myMainReceiver = new MyMainReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(readService.ACTION_UPDATE_ACCELEROMETER);
//        registerReceiver(myMainReceiver,intentFilter);
//        pulseReceiver = new HrReceiver(); //suraj
//        IntentFilter HRfilter = new IntentFilter();
//        HRfilter.addAction("HR_UPDATE");
//        registerReceiver(pulseReceiver,HRfilter);
//        super.onStart();
//    }
//
//
//    private class MyMainReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context,Intent intent){
//            String action = intent.getAction();
//            if(action.equals(readService.ACTION_UPDATE_ACCELEROMETER)){
//                Bundle getAccUpdate = intent.getExtras();
//                xValue = getAccUpdate.getString("Xaxis");
//                yValue = getAccUpdate.getString("Yaxis");
//                zValue = getAccUpdate.getString("Zaxis");
//                xAccel = getAccUpdate.getDouble("Xacc");
//                yAccel = getAccUpdate.getDouble("Yacc");
//                zAccel = getAccUpdate.getDouble("Zacc");
//
//                predictPosture();
//            }
//        }
//    }
//
//    private class HrReceiver extends BroadcastReceiver { //suraj
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("HR_UPDATE")) {
//                Bundle HrUpdate = intent.getExtras();
//                pulserate = HrUpdate.getFloat("pulse");
//
//                heartCondition();
//            }
//        }
//    }
//
//    public void predictPosture(){
//        if ((xAccel < 0.5) && (yAccel < 0.5) && (zAccel > 8))
//        {
//
//            fall = true;
//            Toast.makeText(this, "Falling centre", Toast.LENGTH_SHORT).show();
//        }else if ((xAccel < 0.5) && (yAccel > 0.5) && (zAccel > 8))
//        {
//
//            fall = false;
//            //Toast.makeText(this, "Sitting", Toast.LENGTH_SHORT).show();
//        }else if ((xAccel>5)&&(yAccel<2)&&(zAccel > 4)){
//
//            fall = true;
//            //Toast.makeText(this, "Falling right", Toast.LENGTH_SHORT).show();
//        }else if ((xAccel<-5)&&(yAccel<2)&&(zAccel<4)){
//
//            fall = true;
//            //Toast.makeText(this, "Falling left", Toast.LENGTH_SHORT).show();
//        }else if ((xAccel<2)&&(yAccel>8)&&(zAccel<5)){
//
//            fall = false;
//           // Toast.makeText(this, "standing", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void heartCondition(){
//        if(fall) {
//            if (pulserate < 500f) {
//                if (first = true) {
//                    first = false;
//                    oldPulse = pulserate;
//                    start = System.currentTimeMillis();
//                } else {
//                    newPulse = pulserate;
//                    end = System.currentTimeMillis();
//                    first = true;
//                    oldPulse = newPulse;
//                }
//                if ((end - start) > 30000) {
//                    Intent interrupt = new Intent();
//                    interrupt.setAction("emergency");
//                    interrupt.putExtra("HeartAttack", SOS);
//                    sendBroadcast(interrupt);
//                }
//
//            }
//        }else  {
//            Toast.makeText(this, "Checks sensors", Toast.LENGTH_SHORT).show();
//        }
//    }




}



