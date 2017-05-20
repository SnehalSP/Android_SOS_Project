package com.example.android.message_sqlite;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SOS_service extends Service {

    public static final String KEY_USERNAME = "username";
    private SharedPreferences sharedPreferences;
    private static final String MyPrefs = "TestPreferences";
    public static final String LOGIN_URL = GlobalVariables.serverBaseURL + "/getClosestUser.php?username=";
    public String sms_body;
    public String get_username;
    public String get_phoneNo;




    public SOS_service() {
        //System.out.println("starting///");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    public void onCreate() {
        Toast.makeText(this, " SOS_service Created", Toast.LENGTH_LONG).show();
      // getResponse();
        System.out.println("entered into oncreate of sosservice");

        // (new sfsd(){ } )

    }


@Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Toast.makeText(this, "ONSTART OF SMS Service Started", Toast.LENGTH_LONG).show();

        sms_body=intent.getStringExtra("key_smsURL");
        get_username =intent.getStringExtra("key_phoneNo");
        get_phoneNo=intent.getStringExtra("key_username");
    System.out.println(sms_body);
    System.out.println(get_username);
    System.out.println( get_phoneNo);
//        url.putExtra("key_smsURL",sms);
//        url.putExtra("key_phoneNo ",phoneNo);
//        url.putExtra("key_username",username);
////        public String sms_body;
//        public String get_username;
//        public String get_phoneNo;
       // sendAlerts();
        System.out.println("entered into onstart of sosservice");

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(get_phoneNo, null, sms_body, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
         onDestroy();
    return super.onStartCommand(intent, flags, startid);

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();

    }


//    public void getResponse() {
//
//        sharedPreferences = getSharedPreferences(MyPrefs, MODE_PRIVATE);
//        final String user_result = sharedPreferences.getString("Susername", "default string");
//        System.out.println("Resulted Value in shared pref on button click : " + user_result);
//        final String username = user_result.trim();
//        System.out.println("Resulted Value in shared pref : " + username);
//
//        String url = LOGIN_URL + username;
//        System.out.println("Resulted Value in urlf in message service : " + url);
//        //StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                System.out.println("Resulted Value in : " + response);
//                //  String jsonResult = showJSON(response);
//                System.out.println("jsonResult: " + response);
//                showJSON(response);
//
//            }
//        },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(SOS_service.this, error.toString(), Toast.LENGTH_LONG).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put(KEY_USERNAME, username);
////                        map.put(KEY_USER_CONTACT, username);
////                        map.put(KEY_EMERGENCY_CONTACT, username);
////                        map.put(KEY_LATITUDE, latitude);
////                        map.put(KEY_PASSWORD, password);
//                return map;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(SOS_service.this);
//        requestQueue.add(stringRequest);
//
//
//    }
//
//
//    public void showJSON(String response) {
//        User currentUser;
//        String emergency_contact = "";
//        String address = "";
//        String nearbyuser = "";
//        String user_contact = "";
//        String latitude = "";
//        String longitude = "";
//
//        try {
//
//            JSONObject jsonObject = new JSONObject(response);
//
//            System.out.println("jason object : ");
//
////            {"result":{"emergency_contact":"+16619036222","latitude":"39.046281","longitude":"-77.474723","closest_people":[{"username":"Sarosh","latitude":"39.046281","longitude":"-77.474723","user_contact":"+17035418471"},{"username":"Suraj","latitude":"39.046281","longitude":"-77.474723","user_contact":"+15715980793"}]}}
//            if (jsonObject.has("result")) {
//                JSONObject resultObj = jsonObject.getJSONObject("result");
//                currentUser = new User();
//                sharedPreferences = getSharedPreferences(MyPrefs, MODE_PRIVATE);
//                final String user_result = sharedPreferences.getString("Susername", "default string");
//                final String username = user_result.trim();
//                currentUser.username = username;
//                currentUser.emergencyContact = resultObj.getString("emergency_contact");
//                currentUser.latitude = resultObj.getString("latitude");
//                currentUser.longitude = resultObj.getString("longitude");
//
//                List<SOS_service.User> closestUsers = new ArrayList<>();
//                JSONArray closest_people = resultObj.getJSONArray("closest_people");
//                for (int i = 0; i < closest_people.length(); i++) {
//                    JSONObject person = closest_people.getJSONObject(i);
//                    User user = new User();
//                    user.username = person.getString("username");
//                    user.latitude = person.getString("latitude");
//                    user.longitude = person.getString("longitude");
//                    user.userContact = person.getString("user_contact");
//                    closestUsers.add(user);
//                }
//                sendAlerts(currentUser, closestUsers);
//
//            } else {
//                System.out.println("Incorrect format of result received");
//            }
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//        // textViewContact.setText("Emergenvy contact:\t" +emergency_contact);
//    }

//    public class User {
//        public String username;
//        public String latitude;
//        public String longitude;
//        public String userContact;
//        public String emergencyContact;
//
//        @Override
//        public String toString() {
//            return "{" + this.username + " " + this.latitude + " " + this.longitude + " " + this.userContact + " " + this.emergencyContact + "}";
//        }
//
//    }
////
//
//    public void sendAlerts(final User currentUser, final List<User> closertUsers)
//    {
//
////        System.out.println("currentUser");
////        System.out.println(currentUser.toString());
////        for(User user: closertUsers){
////            System.out.println("closest: ");
////            System.out.println(user.toString());
////        }
//
//
//        //   for map
//        // Button coordinates = (Button) findViewById(R.id.btnShowLocation);
//        //coordinates.setOnClickListener(new View.OnClickListener()
//        //{
//        //  @Override
//        //public void onClick(View v) {
//        //currentUser = new User();
//        String dlat = currentUser.latitude.toString();
//        String dlng = currentUser.longitude.toString();
//        // String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=40.730610,-73.935242 ");
//        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr="+dlat+","+dlng);
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//        intent.setPackage("com.google.android.apps.maps");
//        try
//        {
//            startActivity(intent);
//        }
//
//        catch(ActivityNotFoundException ex)
//        {
//            try
//            {
//                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                startActivity(unrestrictedIntent);
//            }
//            catch(ActivityNotFoundException innerEx)
//            {
//                Toast.makeText(SOS_service.this, "Please install a maps application", Toast.LENGTH_LONG).show();
//            }
//        }
//
//        // getting the phone number
//        for(User user: closertUsers){
//            System.out.println("insidemap: ");
//            System.out.println(user.userContact.toString());
//
//            String phoneNo =  user.userContact.toString();
//            String username =  user.username.toString();
//
//            System.out.println(phoneNo);
//            System.out.println(username);
//
//            String sms = "Hi "+username+ " Help is needed at location:"+uri;
//            try {
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(phoneNo, null, sms, null, null);
//                Toast.makeText(getApplicationContext(), "SMS Sent!",
//                        Toast.LENGTH_LONG).show();
//            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(),
//                        "SMS failed, please try again later!",
//                        Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }
//
//        }
//    }

//    private void sendSMS(String phoneNumber, String message) {
////        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
////        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
////        PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0,
////                new Intent(mContext, SmsSentReceiver.class), 0);
////        PendingIntent deliveredPI = PendingIntent.getBroadcast(mContext, 0,
////                new Intent(mContext, SmsDeliveredReceiver.class), 0);
//        try {
//            SmsManager sms = SmsManager.getDefault();
//            ArrayList<String> mSMSMessage = sms.divideMessage(message);
//            for (int i = 0; i < mSMSMessage.size(); i++) {
//                sentPendingIntents.add(i, sentPI);
//                deliveredPendingIntents.add(i, deliveredPI);
//            }
//            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
//                    sentPendingIntents, deliveredPendingIntents);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            Toast.makeText(getBaseContext(), "SMS sending failed...",Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
//



}