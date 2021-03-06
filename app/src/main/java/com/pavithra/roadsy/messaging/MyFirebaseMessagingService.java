package com.pavithra.roadsy.messaging;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pavithra.roadsy.ClientRequestDisplay;
import com.pavithra.roadsy.User;
import com.pavithra.roadsy.location.Location;
import com.pavithra.roadsy.request_service.AdditionalDetails;
import com.pavithra.roadsy.request_service.AdditionalServiceRequestDetail;
import com.pavithra.roadsy.request_service.RequiredService;
import com.pavithra.roadsy.request_service.ServiceRequestCall;
import com.pavithra.roadsy.util.AppUtill;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Map<String,String> data=remoteMessage.getData();
                JSONObject jsonObject;
                try {

                    jsonObject = new JSONObject(data.get("stringData"));
                    System.out.println("HJHJHJ " +jsonObject);

                    if (jsonObject.isNull("status")) {

                        JSONObject additionalServiceRequestDetailJSONObj = new JSONObject(jsonObject.getString("additionalServiceRequestDetail"));
                        JSONArray requiredServiceListJSONArray = new JSONArray(jsonObject.getString("requiredServiceList"));
                        JSONObject serviceProviderJSONObj = new JSONObject(jsonObject.getString("serviceProvider"));
                        JSONObject clientJSONObj = new JSONObject(jsonObject.getString("client"));
                        System.out.println(clientJSONObj);
                        System.out.println(clientJSONObj.getString("location"));

                        AdditionalServiceRequestDetail additionalServiceRequestDetail = new AdditionalServiceRequestDetail(additionalServiceRequestDetailJSONObj.getString("description"), Boolean.getBoolean(additionalServiceRequestDetailJSONObj.getString("isToolsAvailable")));

                        JSONObject locationServiceProviderJSONObj = new JSONObject(serviceProviderJSONObj.getString("location"));
                        Location locationServiceProvider = new Location(locationServiceProviderJSONObj.getString("longitude"), locationServiceProviderJSONObj.getString("latitude"));
                        User serviceProvider = new User(serviceProviderJSONObj.getString("name"), serviceProviderJSONObj.getString("email"), "mechanic", serviceProviderJSONObj.getString("telephone"), serviceProviderJSONObj.getString("fcmToken"), serviceProviderJSONObj.getString("businessRegistrationNumber"), locationServiceProvider);
                        serviceProvider.setFirebaseUid(serviceProviderJSONObj.getString("firebaseUid"));

                        JSONObject locationClientJSONObj = new JSONObject(clientJSONObj.getString("location"));
                        Location locationClient = new Location(locationClientJSONObj.getString("longitude"), locationClientJSONObj.getString("latitude"));

                        User client = new User(clientJSONObj.getString("name"), clientJSONObj.getString("email"), "user", clientJSONObj.getString("telephone"), clientJSONObj.getString("fcmToken"), locationClient);
                        client.setFirebaseUid(clientJSONObj.getString("firebaseUid"));

                        List<RequiredService> requiredServicesList = new ArrayList<>();

                        for (int i = 0; i < requiredServiceListJSONArray.length(); i++) {
                            RequiredService requiredService = new RequiredService(requiredServiceListJSONArray.getJSONObject(i).getString("name"), requiredServiceListJSONArray.getJSONObject(i).getString("isRequired"));
                            requiredServicesList.add(requiredService);
                        }

                        ServiceRequestCall serviceRequestCall = new ServiceRequestCall(requiredServicesList, client, serviceProvider, additionalServiceRequestDetail);

                        Intent intent = new Intent(getApplicationContext(), ClientRequestDisplay.class);
                        intent.putExtra("service-request-call-for-mechanic", serviceRequestCall);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);


                }else{
                        Toast.makeText(getApplicationContext(), jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent("android.intent.action.ServiceStatusUpdate").putExtra("service_status", jsonObject.getString("status"));
                        getApplicationContext().sendBroadcast(i);
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Somthing bad happened!!! \n json parsing went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference("users").child(user.getUid());
        databaseReference.child("fcmToken").setValue(s);
    }


}
